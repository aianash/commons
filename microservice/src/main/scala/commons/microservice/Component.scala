package aianonymous.commons.microservice

import akka.actor.ActorSystem
import akka.cluster.Cluster


/** A component in micro service architecture
  *
  */
trait Component {

  def name: String
  def start(system: ActorSystem)
  def runOnRole: String

  final def ifCanRunOn[T](cluster: Cluster)(thunk: Component => T): Unit =
    if(canRunOn(cluster)) thunk(this)

  /** Override this for custom logic
    */
  def canRunOn(cluster: Cluster): Boolean =
    cluster.selfRoles.contains(runOnRole)

}