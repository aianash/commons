package commons
package catalogue

import core.util.UnsafeUtil
import memory._


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
    itemTypeGroupOf(binary) match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt(binary)
      case ItemType.Clothing           => items.Clothing(binary)
      case _                           => throw new IllegalArgumentException("This item type is not yet implemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte], brandItem: CatalogueItem) =
    itemTypeGroupOf(binary) match {
      case ItemType.MensPoloNeckTShirt => items.MensPoloNeckTShirt(binary, brandItem)
      case ItemType.Clothing           => items.Clothing(binary, brandItem)
      case _                           => throw new IllegalArgumentException("This item type is not yet implemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(memory: Memory) =
    itemTypeGroupOf(memory.underlying) match {
      case ItemType.MensPoloNeckTShirt => new items.MensPoloNeckTShirt(memory)
      case ItemType.Clothing           => new items.Clothing(memory)
      case _                           => throw new IllegalArgumentException("This item type is not yet implemented")
    }

  def itemIdOf(binary: Array[Byte]): CatalogueItemId =
    CatalogueItemId(UNSAFE.getLong(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.CATALOGUE_ITEM_ID_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemIdOf(memory: Memory): CatalogueItemId =
    itemIdOf(memory.underlying)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeOf(binary: Array[Byte]): ItemType =
    ItemType(UNSAFE.getInt(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.ITEM_TYPE_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeOf(memory: Memory): ItemType =
    itemTypeOf(memory.underlying)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeGroupOf(binary: Array[Byte]): ItemType =
    ItemType(UNSAFE.getInt(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.ITEM_TYPE_GROUP_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeGroupOf(memory: Memory): ItemType =
    itemTypeGroupOf(memory.underlying)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ownerTypeOf(binary: Array[Byte]): OwnerType =
    OwnerType(UNSAFE.getChar(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.OWNER_ID_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ownerTypeOf(memory: Memory): OwnerType =
    ownerTypeOf(memory.underlying)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def compatible(binary: Array[Byte], to: Memory) = {
    (itemTypeGroupOf(binary) >> itemTypeGroupOf(to.underlying)) &&
    (itemTypeOf(binary) eq itemTypeOf(to))
  }

}