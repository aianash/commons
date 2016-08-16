package aianonymous.commons.core.protocols

// A message used for graceful shutdown
case object Shutdown
case class ServiceUnavailable(msg: String)
