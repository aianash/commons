package aianonymous.commons.events.templates.v1

import org.msgpack.ScalaMessagePack

import scalaz._, Scalaz._
import scalaz.std.option._

import aianonymous.commons.events._

object TrackingEventCodec {

  import Implicits._

  def encode(trackingEvent: TrackingEvent): Option[Array[Byte]] =
    trackingEvent match {
      case pfv: PageFragmentView =>
        ScalaMessagePack.writeT(pfv).some
      case sv: SectionView =>
        ScalaMessagePack.writeT(sv).some
      case mp: MousePath =>
        ScalaMessagePack.writeT(mp).some
      case sc: Scanning =>
        ScalaMessagePack.writeT(sc).some
      case _ => None
  }

  def decode(serialized: Array[Byte], eventType: Char): Option[TrackingEvent] = {
    import TrackingEvent.Codes._
    import ScalaMessagePack.messagePack
    eventType match {
      case PAGE_FRAGMENT_VIEW =>
        messagePack.read(serialized, pageFragmentViewTemplate).some
      case SECTION_VIEW =>
        messagePack.read(serialized, sectionViewTemplate).some
      case MOUSE_PATH =>
        messagePack.read(serialized, mousePathTemplate).some
      case SCANNING =>
        messagePack.read(serialized, scanningTemplate).some
      case _ => None
    }
  }

}
