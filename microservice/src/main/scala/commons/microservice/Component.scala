package commons.microservice

import akka.actor.ActorSystem


/** A component in micro service architecture
  *
  */
trait Component {

  def start(system: ActorSystem)
  def identifiedBy: String

}