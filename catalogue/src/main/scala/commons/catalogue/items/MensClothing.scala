package commons.catalogue.items

import commons.catalogue.attributes._


trait MensClothing extends Clothing {

  __appendItemTypeGroup(ItemTypeGroup.MensClothing)

}

object MensClothing {

  // MensClothing inherits from Clothing
  val SEGMENT_IDX = Clothing.SEGMENT_IDX + 1

  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////



  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// MensClothing Builders /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] trait Writers {}

  private[catalogue] abstract class BrandItemBuilder[B <: BrandItemBuilder[B]]
    extends Clothing.BrandItemBuilder[B] with Writers { self: B => }

}