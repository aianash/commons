package commons.catalogue

import commons.core.util.UnsafeUtil


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
trait CatalogueItemUtilMethods {

  import UnsafeUtil._

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte]) =
    itemTypeOf(binary) match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt(binary)
      case _ => throw new IllegalArgumentException("This item type is not yet imeplemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte], brandItem: CatalogueItem) =
    brandItem.itemType match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt(binary, brandItem)
      case _ => throw new IllegalArgumentException("This item type is not yet imeplemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeOf(binary: Array[Byte]) =
    ItemType(UNSAFE.getInt(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.ITEM_TYPE_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ownerTypeOf(binary: Array[Byte]) =
    OwnerType(UNSAFE.getChar(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.OWNER_ID_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def merge[C <: Traversable[CatalogueItem]](items: C): C = {
    ???
  }

}