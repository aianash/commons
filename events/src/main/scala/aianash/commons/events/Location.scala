package aianash.commons.events

import org.joda.time.{Duration, DateTime}
import scala.reflect._

import aianash.commons.events.templates._

/////////////////////////////////////// Identifier classes ////////////////////////////////////////////

case class WebsiteId(stuuid: Long) extends AnyVal
case class ActivityId(actuuid: Long) extends AnyVal
case class PageId(pguuid: Long) extends AnyVal
case class AppId(appuuid: Long) extends AnyVal

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
  websiteId : WebsiteId,
  pageId    : PageId
) extends Location

case class App(
  appId      : AppId,
  activityId : ActivityId
) extends Location