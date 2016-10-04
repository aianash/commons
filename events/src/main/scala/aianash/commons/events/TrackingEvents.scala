package aianash.commons.events

import org.joda.time.{Duration, DateTime}
import scala.reflect._

import aianash.commons.events.templates._

case class TokenId(val tkuuid: Long) extends AnyVal
case class AianId(val anuuid: Long) extends AnyVal
case class PageId(val pguuid: Long) extends AnyVal
case class SessionId(val snuuid: Long) extends AnyVal

case class Position(x: Int, y: Int)

sealed trait TrackingEvent

object TrackingEvent {

  object Codes {
    val PAGE_FRAGMENT_VIEW = 'f'
    val SECTION_VIEW       = 's'
    val MOUSE_PATH         = 'p'
    val SCANNING           = 'r'
    val ACTION             = 'a'
  }

  def codeFor[T <: TrackingEvent : ClassTag] = {
    import TrackingEvent.Codes._
    classTag[T] match {
      case _ if classTag[T] == classTag[PageFragmentView] => PAGE_FRAGMENT_VIEW
      case _ if classTag[T] == classTag[SectionView]      => SECTION_VIEW
      case _ if classTag[T] == classTag[MousePath]        => MOUSE_PATH
      case _ if classTag[T] == classTag[Scanning]         => SCANNING
      case _ if classTag[T] == classTag[Action]           => ACTION
      case _ => 'u'
    }
  }

  def codeFor[T <: TrackingEvent](obj: T) = {
    import TrackingEvent.Codes._
    obj match {
      case _: PageFragmentView => PAGE_FRAGMENT_VIEW
      case _: SectionView      => SECTION_VIEW
      case _: MousePath        => MOUSE_PATH
      case _: Scanning         => SCANNING
      case _: Action           => ACTION
      case _                   => 'u'
    }
  }

  def encode(trackingEvent: TrackingEvent, version: Int): Option[Array[Byte]] =
    version match {
      case 1 =>
        v1.TrackingEventCodec.encode(trackingEvent)
      case _ => None
  }

  def decode(serialized: Array[Byte], eventType: Char, version: Int): Option[TrackingEvent] =
    version match {
      case 1 =>
        v1.TrackingEventCodec.decode(serialized, eventType)
      case _ => None
  }
}

case class PageFragmentView(
  pageId       : PageId,
  scrollPos    : Position,
  windowHeight : Int,
  windowWidth  : Int,
  startTime    : DateTime,
  duration     : Duration
  ) extends TrackingEvent

case class SectionView(
  pageId    : PageId,
  sectionId : Int,
  pos       : Position,
  startTime : DateTime,
  duration  : Duration
  ) extends TrackingEvent

case class MousePath(
  pageId    : PageId,
  sections  : Seq[(Int, Position)],
  startTime : DateTime,
  duration  : Duration
  ) extends TrackingEvent

case class Scanning(
  pageId    : PageId,
  fromPos   : Position,
  toPos     : Position,
  startTime : DateTime,
  duration  : Duration
  ) extends TrackingEvent

case class Action(
  pageId     : PageId,
  timeStamp  : DateTime,
  name       : String,
  props      : Map[String, String]
  ) extends TrackingEvent

case class EventsSession(
  tokenId    : TokenId,
  aianId     : AianId,
  sessionId  : SessionId,
  events     : Seq[TrackingEvent])

