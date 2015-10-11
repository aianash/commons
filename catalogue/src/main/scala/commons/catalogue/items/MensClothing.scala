package commons.catalogue.items

import commons.catalogue.ItemTypeGroup
import commons.catalogue.attributes._
import commons.catalogue.memory.Memory
import commons.owner.OwnerId

class MensClothing(memory: Memory) extends Clothing(memory) {

  import MensClothing._

  override def instanceItemTypeGroup: ItemTypeGroup = ItemTypeGroup.Clothing

  require(this.gender equals Male,
    throw new IllegalArgumentException(s"Item with MensClothing instantiated with $gender"))

  override def canEqual(that: Any) = that match {
    case _: MensClothing => true
    case _ => false
  }

  def asMensClothing =
    new MensClothing(afterItemTypeGroupIsSetTo(memory.truncateTo(SEGMENT_IDX), ItemTypeGroup.MensClothing))

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