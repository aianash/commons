package commons.microservice

import scala.util.Random
import scala.concurrent.duration._

import akka.actor.{Actor, Props, ActorSystem}

import akka.remote.testkit.MultiNodeConfig
import akka.remote.testkit.MultiNodeSpec

import akka.testkit.ImplicitSender

import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, CurrentClusterState}

import com.typesafe.config.ConfigFactory

import org.apache.curator.test.TestingServer


object MicroserviceMultiJvmConfig extends MultiNodeConfig {

  val node1 = role("node1")
  val node2 = role("node2")

  val zkUrl = "127.0.0.1:2181"

  commonConfig(ConfigFactory.parseString(s"""
    akka.actor.provider = akka.cluster.ClusterActorRefProvider

    microservice {
      zookeeper {
        url = "${zkUrl}"
        seed.path = "/cluster/test/seeds"
      }
    }
    """))

}

class MicroserviceSpecMultiJvmNode1 extends MicroserviceSpec
class MicroserviceSpecMultiJvmNode2 extends MicroserviceSpec

class MicroserviceSpec extends MultiNodeSpec(MicroserviceMultiJvmConfig)
  with STMultiNodeSpec
  with ImplicitSender {

  import MicroserviceMultiJvmConfig._

  def initialParticipants = roles.size

  muteDeadLetters(classOf[Any])(system)

  "A microservice" must {

    "start all nodes" in {
      Cluster(system).subscribe(testActor, classOf[MemberUp])
      expectMsgClass(classOf[CurrentClusterState])
      Microservice(system).start(IndexedSeq(ComponentOne, ComponentTwo))

      receiveN(2, 10 seconds).map {
        case MemberUp(m) => m.address
      }.toSet must be(
        Set(node(node1).address, node(node2).address))
      enterBarrier("up")
      Cluster(system).state.members.size must be(2)
      enterBarrier("done")
    }

  }

  class ComponentOneSupervisor extends Actor {
    def receive = {
      case x: String => sender() ! x.toUpperCase
    }
  }

  class ComponentTwoSupervisor extends Actor {
    def receive = {
      case x: String => sender() ! x.toLowerCase
    }
  }

  case object ComponentOne extends Component {
    def identifiedBy = "node1"
    def start(system: ActorSystem) {
      system.actorOf(Props[ComponentOneSupervisor])
    }
  }

  case object ComponentTwo extends Component {
    def identifiedBy = "node2"
    def start(system: ActorSystem) {
      system.actorOf(Props[ComponentTwoSupervisor])
    }
  }

}