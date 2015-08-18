package commons.microservice

import akka.actor.ActorSystem


final class MicroserviceSettings(system: ActorSystem) {

  val config = system.settings.config.getConfig("microservice")

  val LogInfo = config.getBoolean("log-info")
  val ZKUrl = config.getString("zookeeper.url")

  val RetryAttemptForLeaderElection = config.getInt("retry-attempts-for-leader-election")

  val ClusterSeedZkPath = config.getString("zookeeper.seed-path")

  val ServiceHost =
    if(config.hasPath("host")) Some(config.getString("host"))
    else None

  val ServicePort =
    if(config.hasPath("port")) Some(config.getInt("port"))
    else None

}