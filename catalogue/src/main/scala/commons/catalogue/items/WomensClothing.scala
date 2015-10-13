package commons.catalogue.items

import commons.catalogue.ItemTypeGroup
import commons.catalogue.attributes._
import commons.catalogue.memory.Memory
import commons.owner.OwnerId

class WomensClothing(memory: Memory) extends Clothing(memory) {

  import WomensClothing._

  override def instanceItemTypeGroup: ItemTypeGroup = ItemTypeGroup.Clothing

  require(this.gender equals Female,
    throw new IllegalArgumentException(s"Item with WomensClothing instantiated with $gender"))

  override def canEqual(that: Any) = that match {
    case _: WomensClothing => true
    case _ => false
  }

  def asWomensClothing =
    new WomensClothing(afterItemTypeGroupIsSetTo(memory.truncateTo(SEGMENT_IDX), ItemTypeGroup.WomensClothing))

}


object WomensClothing {

  // WomensClothing inherits from Clothing
  val SEGMENT_IDX = Clothing.SEGMENT_IDX + 1

  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////



  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// WOMENSCLOTHING BUILDER ////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends Clothing.Builder[B, OW] { self: B => }

}