package commons.catalogue.items

import commons.catalogue._
import commons.catalogue.attributes._
import commons.catalogue.memory.{Memory, PrimaryMemory, SecondaryMemory}

import commons.owner._


class MensTShirt(memory: Memory) extends MensClothing(memory) {

  import MensTShirt._

  override def instanceItemTypeGroup: ItemTypeGroup = ItemTypeGroup.Clothing

  override def canEqual(that: Any) = that match {
    case _: MensTShirt => true
    case _ => false
  }

}

object MensTShirt {
  import OwnerType._

  val SEGMENT_IDX = MensClothing.SEGMENT_IDX + 1
  val TOTAL_SEGMENTS = SEGMENT_IDX + 1

  private[catalogue] def apply(binary: Array[Byte]) =
    ifBrand(binary) { memory =>
      new MensTShirt(memory)
    }

  private[catalogue] def apply(binary: Array[Byte], brandItem: CatalogueItem) = {
    CatalogueItem.ownerTypeOf(binary) match {
      case STORE if brandItem.ownerId.ownerType.equals(BRAND) && brandItem.isInstanceOf[MensTShirt] =>
        new MensTShirt(SecondaryMemory(binary, brandItem.memory))
      case _ =>
        throw new IllegalArgumentException("OwnerType of parameters is not consistent ie binary with STORE and item with BRAND type")
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// MENSCLOTHING BUILDER //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  def builder = new CatalogueItem.BuilderFatory(new BrandItemBuilder, c => new StoreItemBuilder(c))

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends MensClothing.Builder[B, OW] { self: B =>
    type I = MensTShirt
    val numSegments = TOTAL_SEGMENTS
    val itemTypeGroup = ItemTypeGroup.MensTShirt

    def build() = {
      super.setAttributes()
      new MensTShirt(builder.memory())
    }
  }

  class BrandItemBuilder extends Builder[BrandItemBuilder, BrandId] with CatalogueItem.BrandItemBuilder[BrandItemBuilder]

  class StoreItemBuilder(val brandItem: CatalogueItem) extends Builder[StoreItemBuilder, StoreId] with CatalogueItem.StoreItemBuilder[StoreItemBuilder]

}