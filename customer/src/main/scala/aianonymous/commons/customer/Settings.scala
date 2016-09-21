package aianonymous.commons.customer

case class Instance(
  tokenId   : Long,
  startHour : Int,
  endHour   : Int,
  statOnly  : Boolean,
  name      : String)