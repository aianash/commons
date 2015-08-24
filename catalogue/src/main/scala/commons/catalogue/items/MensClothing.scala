package commons.catalogue.items

import commons.catalogue.ItemType
import commons.catalogue.attributes._
import commons.catalogue.OwnerId
import commons.catalogue.memory.Memory


class MensClothing(memory: Memory) extends Clothing(memory) {

  import MensClothing._

  override def itemTypeGroup = ItemType.MensClothing

  override def canEqual(that: Any) = that match {
    case _: MensClothing => true
    case _ => false
  }

  def asMensClothing =
    new MensClothing(afterItemTypeGroupIsSetTo(memory.truncateTo(SEGMENT_IDX), ItemType.MensClothing))

}


object MensClothing {

  // MensClothing inherits from Clothing
  val SEGMENT_IDX = Clothing.SEGMENT_IDX + 1

  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////



  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// MENSCLOTHING BUILDER //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends Clothing.Builder[B, OW] { self: B => }

}