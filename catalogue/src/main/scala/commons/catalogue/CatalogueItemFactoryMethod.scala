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
  def create(binary: Array[Byte]) =
    itemTypeOf(binary) match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt.create(binary)
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def create(binary: Array[Byte], brandItem: CatalogueItem) =
    brandItem.itemType match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt.create(binary, brandItem)
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeOf(binary: Array[Byte]) =
    ItemType.withCode(unsafe.getInt(BYTE_ARRAY_BASE_OFFSET + CatalogueItem.ITEM_TYPE_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ownerTypeOf(binary: Array[Byte]) =
    OwnerType(unsafe.getChar(BYTE_ARRAY_BASE_OFFSET + CatalogueItem.OWNER_ID_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def merge[C <: Traversable[CatalogueItem]](items: C): C = {
    ???
  }

}