package aianonymous.commons.events

import scala.reflect._

import java.net.URL

import aianonymous.commons.events.templates._

case class Position(x: Int, y: Int)

sealed trait TrackingEvent

object TrackingEvent {

  object Codes {
    val PAGE_FRAGMENT_VIEW = 'f'
    val SECTION_VIEW       = 's'
    val MOUSE_PATH         = 'p'
    val SCANNING           = 'r'
  }

  def codeFor[T <: TrackingEvent : ClassTag] = {
    import TrackingEvent.Codes._
    classTag[T] match {
      case _ if classTag[T] == classTag[PageFragmentView] => PAGE_FRAGMENT_VIEW
      case _ if classTag[T] == classTag[SectionView]      => SECTION_VIEW
      case _ if classTag[T] == classTag[MousePath]        => MOUSE_PATH
      case _ if classTag[T] == classTag[Scanning]         => SCANNING
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
  scrollPos    : Position,
  windowHeight : Int,
  windowWidth  : Int,
  startTime    : Long,
  duration     : Int
  ) extends TrackingEvent

case class SectionView(
  sectionId : Int,
  pos       : Position,
  startTime : Long,
  duration  : Int
  ) extends TrackingEvent

case class MousePath(
  sections  : Seq[(Int, Position)],
  startTime : Long,
  duration  : Int
  ) extends TrackingEvent

case class Scanning(
  fromPos   : Position,
  toPos     : Position,
  startTime : Long,
  duration  : Int
  ) extends TrackingEvent

case class EventsSession(
  tokenId    : Long,
  aianId     : Long,
  sessionId  : Long,
  pageEvents : Seq[PageEvents])

case class PageEvents(
  sessionId : Long,
  pageId    : Long,
  startTime : Long,
  events    : Seq[TrackingEvent])

