package aianash.commons.events

import scala.reflect._

import org.joda.time.{Duration, DateTime}

import play.api.libs.json._

import aianash.commons.events.templates._
import aianash.commons.events._


/////////////////////////////////////// Identifier classes ////////////////////////////////////////////

case class TokenId(tkuuid: Long) extends AnyVal
case class AianId(anuuid: Long) extends AnyVal
case class SessionId(snuuid: Long) extends AnyVal

/////////////////////////////////////// Helper classes ////////////////////////////////////////////

case class Position(x: Int, y: Int)

/////////////////////////////////////// TrackingEvent Traits ////////////////////////////////////////////

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

/////////////////////////////////////// Movement classes ////////////////////////////////////////////

sealed trait Movement

case class PageFragmentView(
  location     : Location,
  scrollPos    : Position,
  windowHeight : Int,
  windowWidth  : Int,
  startTime    : DateTime,
  duration     : Duration
) extends Movement with TrackingEvent

case class SectionView(
  location     : Location,
  sectionId    : Int,
  pos          : Position,
  startTime    : DateTime,
  duration     : Duration
) extends Movement with TrackingEvent

case class MousePath(
  location     : Location,
  sections     : Seq[(Int, Position)],
  startTime    : DateTime,
  duration     : Duration
) extends Movement with TrackingEvent

case class Scanning(
  location     : Location,
  fromPos      : Position,
  toPos        : Position,
  startTime    : DateTime,
  duration     : Duration
) extends Movement with TrackingEvent


/////////////////////////////////////// Action classes ////////////////////////////////////////////

case class Action(
  location   : Location,
  timeStamp  : DateTime,
  name       : String,
  props      : JsObject
) extends TrackingEvent


/////////////////////////////////////// Session classes ////////////////////////////////////////////

case class EventSession(
  tokenId    : TokenId,
  aianId     : AianId,
  sessionId  : SessionId,
  startTime  : DateTime,
  events     : Seq[TrackingEvent])

