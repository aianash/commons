package aianonymous.commons.events

case class ViewEvent(
  scrollTop    : Int,
  scrollLeft   : Int,
  mouseX       : Int,
  mouseY       : Int,
  startTime    : Long,
  duration     : Int,
  windowHeight : Int,
  windowWidth  : Int
  )

case class SectionViewEvent(
  sectionId : Int,
  posX      : Int,
  posY      : Int,
  startTime : Long,
  duration  : Int
  )

case class PathEvent(
  mouseX     : Int,
  mouseY     : Int,
  sectionIds : Seq[Int],
  startTime  : Long,
  duration   : Int
  )