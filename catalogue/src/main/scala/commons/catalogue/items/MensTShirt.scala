package commons.catalogue.items

import commons.catalogue.attributes._
import commons.catalogue.OwnerId


trait MensTShirt extends MensClothing {

  __appendItemTypeGroup(ItemTypeGroup.MensTShirt)

}

object MensTShirt {

  val SEGMENT_IDX = MensClothing.SEGMENT_IDX + 1

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// MENSCLOTHING BUILDER //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends MensClothing.Builder[B, OW] { self: B => }

}