package commons
package catalogue
package test

import akka.testkit._
import akka.actor.ActorSystem

import org.scalatest._

abstract class AkkaUnitSpec(_system: ActorSystem) extends TestKit(_system)
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll
  with GivenWhenThen