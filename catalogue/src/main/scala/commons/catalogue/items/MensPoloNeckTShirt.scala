package commons.catalogue.items

import commons.catalogue._
import commons.catalogue.attributes._
import commons.catalogue.memory.{Memory, PrimaryMemory, SecondaryMemory}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
class MensPoloNeckTShirt(memory: Memory) extends MensTShirt(memory) {

  override def itemTypeGroup = ItemType.MensPoloNeckTShirt

  override def canEqual(that: Any) = that match {
    case MensPoloNeckTShirt => true
    case _ => false
  }

}

/**
 * This object is used to create brand and store items for
 * [[commons.catalogue.items.MensPoloNeckTShirt]]
 * using corresponding builders
 */
object MensPoloNeckTShirt {

  val SEGMENT_IDX = MensTShirt.SEGMENT_IDX + 1
  val TOTAL_SEGMENTS = SEGMENT_IDX + 1

  private[catalogue] def apply(binary: Array[Byte]) = {
    CatalogueItem.ownerTypeOf(binary) match {
      case BRAND => new MensPoloNeckTShirt(PrimaryMemory(binary))
      case _ => throw new IllegalArgumentException("OwnerType of parameters not BRAND")
    }
  }

  private[catalogue] def apply(binary: Array[Byte], brandItem: CatalogueItem) = {
    CatalogueItem.ownerTypeOf(binary) match {
      case STORE if brandItem.ownerId.ownerType.equals(BRAND) && brandItem.isInstanceOf[MensPoloNeckTShirt] =>
        new MensPoloNeckTShirt(SecondaryMemory(binary, brandItem.memory))
      case _ =>
        throw new IllegalArgumentException("OwnerType of parameters is not consistent ie binary with STORE and item with BRAND type")
    }
  }

  def builder = new CatalogueItem.BuilderFatory(new BrandItemBuilder, c => new StoreItemBuilder(c))

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends MensTShirt.Builder[B, OW] { self: B =>
    type I = MensPoloNeckTShirt
    val numSegments = TOTAL_SEGMENTS
    val itemType = ItemType.MensPoloNeckTShirt

    def build() = {
      super.setAttributes()
      new MensPoloNeckTShirt(builder.memory())
    }
  }


  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  class BrandItemBuilder extends Builder[BrandItemBuilder, BrandId] with CatalogueItem.BrandItemBuilder[BrandItemBuilder]

  class StoreItemBuilder(val brandItem: CatalogueItem) extends Builder[StoreItemBuilder, StoreId] with CatalogueItem.StoreItemBuilder[StoreItemBuilder]

}