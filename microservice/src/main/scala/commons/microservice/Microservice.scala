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
  * Usage:
  * {{{
  * case object UserComponent extends Component {
  *   val identifiedBy = "user"
  *   def start(system) {
  *     system.actorOf(UserSupervisor.props, "user")
  *   }
  * }
  *
  * val system = Microservice("service name", IndexedSeq(UserComponent))
  *
  * // do other things with system
  * }}}
  */
object Microservice extends ExtensionId[Microservice] with ExtensionIdProvider {

  override def get(system: ActorSystem): Microservice = super.get(system)

  override def createExtension(system: ExtendedActorSystem): Microservice =
    new Microservice(system)

  override def lookup() = Microservice

  def apply(name: String, components: IndexedSeq[Component]): ActorSystem = {
    val system = ActorSystem(name) // This loads the ClusterActorRefProvider which then
                                   // initialize and load the Cluster extension
                                   // Although this node has not yet joined others

    // Then we start each component on the cluster
    components.foreach { component =>
      if(Cluster(system).selfRoles.contains(component.identifiedBy)) {
        component.start(system)
      }
    }

    // This joins this Cluster node to other
    // and thus making it reachable from
    // other nodes
    Microservice(system).join()
    system
  }

}


class Microservice(system: ExtendedActorSystem) extends Extension {

  val settings = new MicroserviceSettings(system)

  val zkSeedPath = settings.ClusterSeedZkPath

  // While running inside a container like Docker
  // we need container's external address
  // and port, which can be provided using config
  // microservice.host and microservice.port
  val address =
    if(settings.ServiceHost.nonEmpty && settings.ServicePort.nonEmpty)
      Cluster(system).selfAddress
        .copy(host = settings.ServiceHost, port = settings.ServicePort)
    else Cluster(system).selfAddress

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

  /**
   * Gets seed nodes from zookeeper, creates an entry for this node
   * and joins this node to the cluster
   * NOTE: Should be called only once
   */
  private[microservice] def join() {
    new EnsurePath(zkSeedPath).ensure(curator.getZookeeperClient())
    curator.create().withMode(CreateMode.EPHEMERAL).forPath(zkSeedPath + "/" + zkMyId)
    val nodes = curator.getChildren().forPath(zkSeedPath).filterNot(_.equals(zkMyId))
    if(nodes.isEmpty) {
      Cluster(system).join(address)
    } else {
      val seeds = nodes.map(n => AddressFromURIString(s"akka://$n"))
      Cluster(system).joinSeedNodes(immutable.Seq(seeds: _*))
    }
  }

}