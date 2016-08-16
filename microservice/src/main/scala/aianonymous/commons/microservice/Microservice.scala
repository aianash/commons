package aianonymous.commons.microservice

import collection.JavaConversions._
import scala.concurrent.Future
import scala.collection.immutable.{Seq => ImmutableSeq}
import scala.util.control.Exception._

import akka.actor._
import akka.cluster.Cluster
import akka.event.Logging

import org.apache.zookeeper.CreateMode

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.leader.LeaderLatch
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.utils.EnsurePath

import com.typesafe.config.Config


/** This extension is used for starting cluster using
  * seed nodes from zookeeper
  *
  * IMP: Do not provide seed nodes in the Akka Cluster config
  * because that will automatically be used by AkkaCluster
  * and hence Microservice join request will be ignored
  *
  * Usage:
  * {{{
  * case object UserComponent extends Component {
  *   val name = "user"
  *   val runOnRole = "user"
  *   def start(system: ActorSystem) {
  *     system.actorOf(UserSupervisor.props, name)
  *   }
  * }
  *
  * val system = ActorSystem("name", config)
  * Microservice(system).start(IndexedSeq(UserComponent))
  *
  * // do other things with system, if you want to
  * }}}
  */
object Microservice extends ExtensionId[Microservice] with ExtensionIdProvider {

  override def get(system: ActorSystem): Microservice = super.get(system)

  override def createExtension(system: ExtendedActorSystem): Microservice =
    new Microservice(system)

  override def lookup() = Microservice

}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
class Microservice(system: ExtendedActorSystem) extends Extension {
  import InfoLogger._

  val settings = new MicroserviceSettings(system)
  import settings.LogInfo

  private val log = Logging(system, getClass.getName)

  val zkSeedPath = settings.ClusterSeedZkPath

  private val cluster = Cluster(system)

  // While running inside a container like Docker
  // we need container's external address
  // and port, which can be provided using config
  // microservice.host and microservice.port
  val address =
    if(settings.ServiceHost.nonEmpty && settings.ServicePort.nonEmpty)
        cluster.selfAddress.copy(host = settings.ServiceHost, port = settings.ServicePort)
    else cluster.selfAddress

  private val curator = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    CuratorFrameworkFactory.builder()
      .connectString(settings.ZKUrl)
      .retryPolicy(retryPolicy)
      .build()
  }

  curator.start()

  val zkMyId = address.hostPort

  // Leader latch is used because at the very start if there are
  // two nodes then both should decide which one to make the first
  // seed nodes
  // Hence leadership based selection is better
  val leaderLatch = new LeaderLatch(curator, zkSeedPath, zkMyId)

  system.registerOnTermination {
    ignoring(classOf[IllegalStateException]) {
      logInfo("Closing latch and zookeeper connection")
      leaderLatch.close()
      curator.close()
    }
  }

  /** Start all the applicable components and
    * join this node with other cluster using
    * seeds from zookeeper
    *
    * @param components   Components to start
    */
  def start(components: IndexedSeq[Component]) {
    components.foreach { _.ifCanRunOn(cluster) { _.start(system) } }
    join()
    logInfo("started components [{}]", components.map(_.name).reduceLeft(_ + ", " + _))
  }

  /**
   * Gets seed nodes from zookeeper, creates an entry for this node
   * and joins this node to the cluster
   * NOTE: Should be called only once
   */
  private def join() {
    logInfo("Joining cluster using seed nodes from zookeeper")

    curator.create().creatingParentContainersIfNeeded().forPath(zkSeedPath)
    leaderLatch.start()

    var joined = false;
    var attempt = settings.RetryAttemptForLeaderElection

    // Loop until joined or attepts exhausted
    while((!joined) && (attempt > 0)) {
      val possibleLeader = leaderLatch.getLeader
      if(possibleLeader.isLeader) {
        if(possibleLeader.getId == zkMyId) {
          cluster.join(address)
          joined = true
        } else {
          val seeds =
            leaderLatch.getParticipants
                       .filterNot(_.getId == zkMyId)
                       .map { n => AddressFromURIString(s"akka.tcp://${n.getId}") }
                       .toSeq
          cluster.joinSeedNodes(ImmutableSeq(seeds: _*))
          joined = true
        }
      }
      attempt -= 1
      if(!joined) log.warning(s"Failed to join node [{}], trying again, $attempt left", cluster.selfAddress)
    }

    if(!joined) {
      log.error("Microservice couldnot join this Node [{}] to cluster, Hence stopping the ActorSystem", cluster.selfAddress)
      system.terminate()
    }
  }

  private[microservice] object InfoLogger {

    def logInfo(message: String): Unit =
      if (LogInfo) log.info("Microservice Node [{}] - {}", cluster.selfAddress, message)

    def logInfo(template: String, arg1: Any): Unit =
      if (LogInfo) log.info("Microservice Node [{}] - " + template, cluster.selfAddress, arg1)

    def logInfo(template: String, arg1: Any, arg2: Any): Unit =
      if (LogInfo) log.info("Microservice Node [{}] - " + template, cluster.selfAddress, arg1, arg2)
  }

}

