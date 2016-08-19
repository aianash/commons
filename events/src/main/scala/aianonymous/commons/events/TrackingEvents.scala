package aianonymous.commons.events

case class Position(x: Int, y: Int)

case class ViewEvent(
  scrollPos    : Position,
  mousePos     : Position,
  startTime    : Long,
  duration     : Int,
  windowHeight : Int,
  windowWidth  : Int
  )

case class SectionViewEvent(
  sectionId : Int,
  pos       : Position,
  startTime : Long,
  duration  : Int
  )

case class PathEvent(
  mouseStart : Position,
  mouseEnd   : Position,
  sectionIds : Seq[Int],
  startTime  : Long,
  duration   : Int
  )