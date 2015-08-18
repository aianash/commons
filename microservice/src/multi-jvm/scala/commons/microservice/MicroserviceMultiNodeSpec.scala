package commons.microservice

import scala.util.{Random, Properties}
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

  val zookeeper = role("zookeeper")
  val node1 = role("node1")
  val node2 = role("node2")

  val zkUrl = "localhost:2181"

  def nodeList = Seq(node1, node2)

  // Assigne node with their corresponding cluster
  // roles. This enables Microservice to
  // start corresponding actors
  nodeList foreach { role =>
    nodeConfig(role) {
      ConfigFactory.parseString(s"""
        akka.actor.provider = akka.cluster.ClusterActorRefProvider
        akka.cluster.roles = [${role.name}]
        microservice {
          zookeeper {
            url = "${zkUrl}"
            seed-path = "/cluster/test/seeds"
          }
        }
        """)
    }
  }

  commonConfig(ConfigFactory.parseString(s"""
    akka.remote.log-remote-lifecycle-events = off
    akka.loglevel = ${Properties.envOrElse("LOG_LEVEL", "INFO")}
    akka.loggers = ["akka.testkit.TestEventListener"]
    akka.log-dead-letters-during-shutdown = false
    akka.cluster.metrics.collector-class = akka.cluster.JmxMetricsCollector
    """))
}

class MicroserviceSpecMultiJvmZookeeper extends MicroserviceSpec
class MicroserviceSpecMultiJvmNode1 extends MicroserviceSpec
class MicroserviceSpecMultiJvmNode2 extends MicroserviceSpec

class MicroserviceSpec extends MultiNodeSpec(MicroserviceMultiJvmConfig)
  with STMultiNodeSpec
  with ImplicitSender {

  import MicroserviceMultiJvmConfig._

  def initialParticipants = roles.size

  muteDeadLetters(classOf[Any])(system)

  "A microservice" must {

    "start all nodes with corresponding actors and reach actors" in {
      runOn(zookeeper) {
        val server = new TestingServer(2181)
        system.registerOnTermination {
          system.log.info("Stopping Test zookeeper server")
          server.close()
        }
        enterBarrier("zookeeper-up")
      }

      runOn(node1, node2) {
        enterBarrier("zookeeper-up")
        Cluster(system).subscribe(testActor, classOf[MemberUp])
        expectMsgClass(classOf[CurrentClusterState])

        Microservice(system).start(IndexedSeq(ComponentOne, ComponentTwo))
        receiveN(2, 5 seconds).map {
          case MemberUp(m) => m.address
        }.toSet must be(
          Set(node(node1).address, node(node2).address))

        Cluster(system).state.members.size must be(2)
        Cluster(system).unsubscribe(testActor)

        // Test reachability of actors

        val one = system.actorSelection(node(node1) / "user" / "component-one")
        one ! "hello"
        expectMsg(5.seconds, "HELLO")

        val two = system.actorSelection(node(node2) / "user" / "component-two")
        two ! "HELLO"
        expectMsg(5.seconds, "hello")

        enterBarrier("finished")
      }
    }
  }

}

case object ComponentOne extends Component {
  def name = "component-one"
  def runOnRole = "node1"
  def start(system: ActorSystem) {
    system.actorOf(Props[ComponentOneSupervisor], name)
  }
}

case object ComponentTwo extends Component {
  def name = "component-two"
  def runOnRole = "node2"
  def start(system: ActorSystem) {
    system.actorOf(Props[ComponentTwoSupervisor], name)
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