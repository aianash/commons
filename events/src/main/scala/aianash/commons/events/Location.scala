package aianash.commons.events

import org.joda.time.{Duration, DateTime}
import scala.reflect._

import aianash.commons.events.templates._

/////////////////////////////////////// Identifier classes ////////////////////////////////////////////

case class TokenId(val tkuuid: Long) extends AnyVal
case class PageId(val pguuid: Long) extends AnyVal
case class ActivityId(val actuuid: Long) extends AnyVal

/////////////////////////////////////// Location classes ////////////////////////////////////////////

sealed trait Location

object Location {

  object Codes {
    val WEB_PAGE = 'w'
    val APP     = 'a'
  }

  def codeFor[T <: Location : ClassTag] = {
    import Location.Codes._
    classTag[T] match {
      case _ if classTag[T] == classTag[WebPage] => WEB_PAGE
      case _ if classTag[T] == classTag[App]     => APP
      case _ => 'u'
    }
  }

  def codeFor[T <: Location](obj: T) = {
    import Location.Codes._
    obj match {
      case _: WebPage => WEB_PAGE
      case _: App     => APP
      case _          => 'u'
    }
  }

}

case class WebPage(
  tokenId : TokenId,
  pageId  : PageId
  ) extends Location

case class App(
  tokenId    : TokenId,
  activityId : ActivityId
) extends Location