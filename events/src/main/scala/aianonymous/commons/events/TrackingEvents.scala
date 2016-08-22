package aianonymous.commons.events

import java.net.URL

case class Position(x: Int, y: Int)

sealed trait TrackingEvent

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

case class EventSession(
  tokenId   : Long,
  aianId    : Long,
  sessionId : Long,
  startTime : Long,
  source    : URL,
  events    : Seq[TrackingEvent])