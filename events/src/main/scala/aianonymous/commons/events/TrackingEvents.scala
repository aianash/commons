package aianonymous.commons.events

case class Position(x: Int, y: Int)

sealed trait TrackingEvent

case class PageFragmentView(
  scrollPos    : Position,
  mousePos     : Position,
  startTime    : Long,
  duration     : Int,
  windowHeight : Int,
  windowWidth  : Int
  ) extends TrackingEvent

case class SectionView(
  sectionId : Int,
  pos       : Position,
  startTime : Long,
  duration  : Int
  ) extends TrackingEvent

case class MousePath(
  mouseStart : Position,
  mouseEnd   : Position,
  sectionIds : Seq[Int],
  startTime  : Long,
  duration   : Int
  ) extends TrackingEvent