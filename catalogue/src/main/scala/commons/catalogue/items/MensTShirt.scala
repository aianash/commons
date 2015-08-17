package commons.catalogue.items

import commons.catalogue.attributes._


trait MensTShirt extends MensClothing {

  __appendItemTypeGroup(ItemTypeGroup.MensTShirt)

}

object MensTShirt {

  val SEGMENT_IDX = MensClothing.SEGMENT_IDX + 1

  private[catalogue] trait Writers {}

  private[catalogue] abstract class BrandItemBuilder[B <: BrandItemBuilder[B]]
    extends MensClothing.BrandItemBuilder[B] with Writers { self: B => }
}