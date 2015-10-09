package commons.catalogue.items

import commons.catalogue.ItemTypeGroup
import commons.catalogue.attributes._
import commons.catalogue.OwnerId
import commons.catalogue.memory.Memory


class MensTShirt(memory: Memory) extends MensClothing(memory) {

  import MensTShirt._

  override def instanceItemTypeGroup: ItemTypeGroup = ItemTypeGroup.Clothing

  override def canEqual(that: Any) = that match {
    case _: MensTShirt => true
    case _ => false
  }

}


object MensTShirt {

  val SEGMENT_IDX = MensClothing.SEGMENT_IDX + 1

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// MENSCLOTHING BUILDER //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends MensClothing.Builder[B, OW] { self: B => }

}