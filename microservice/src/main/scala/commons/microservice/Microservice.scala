package commons.microservice

import collection.JavaConversions._
import scala.concurrent.Future
import scala.collection.immutable
import scala.util.control.Exception._

import akka.actor._
import akka.cluster.Cluster

import org.apache.zookeeper.CreateMode

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.utils.EnsurePath

import com.typesafe.config.Config


/** This extension is used for starting cluster using
  * seed nodes from zookeeper
  *
  * IMP: Do not provide seed nodes in the Akka Cluster config
  * because that will automatically join the node to cluster
  * and then Microservice join will be ignored
  *
  * Usage:
  * {{{
  * case object UserComponent extends Component {
  *   val identifiedBy = "user"
  *   def start(system: ActorSystem) {
  *     system.actorOf(UserSupervisor.props, "user")
  *   }
  * }
  *
  * val system = ActorSystem("name", config)
  * Microservice(system).start(IndexedSeq(UserComponent))
  *
  * // do other things with system
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

  val settings = new MicroserviceSettings(system)

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

  val zkMyId = address.hostPort

  curator.start()

  system.registerOnTermination {
    ignoring(classOf[IllegalStateException]) {
      curator.close()
    }
  }

  /** Start the component and join this
    * node to the cluster
    *
    * @param components   Components to start
    */
  def start(components: IndexedSeq[Component]) {
    components.foreach { component =>
      if(cluster.selfRoles.contains(component.identifiedBy)) {
        component.start(system)
      }
    }
    join()
  }

  /**
   * Gets seed nodes from zookeeper, creates an entry for this node
   * and joins this node to the cluster
   * NOTE: Should be called only once
   */
  private def join() {
    new EnsurePath(zkSeedPath).ensure(curator.getZookeeperClient())
    val s = curator.create().withMode(CreateMode.EPHEMERAL).forPath(zkSeedPath + "/" + zkMyId)
    val nodes = curator.getChildren().forPath(zkSeedPath).filterNot(_.equals(zkMyId))
    if(nodes.isEmpty) {
      println("hjhjhjhj")
      cluster.join(address)
    } else {
      val seeds = nodes.map(n => AddressFromURIString(s"akka.tcp://$n"))
      cluster.joinSeedNodes(immutable.Seq(seeds: _*))
    }
  }

}