package commons
package catalogue

import core.util.UnsafeUtil
import memory._
import owner._

import scalaz._, Scalaz._


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
trait CatalogueItemUtilMethods {

  import UnsafeUtil._
  import OwnerType._

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte]) =
    itemTypeGroupOf(binary) match {
      case ItemTypeGroup.MensTShirt => items.MensTShirt(binary)
      case ItemTypeGroup.Clothing   => items.Clothing(binary)
      case _                        => throw new IllegalArgumentException("This item type is not yet implemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte], brandItem: CatalogueItem) =
    itemTypeGroupOf(binary) match {
      case ItemTypeGroup.MensTShirt => items.MensTShirt(binary, brandItem)
      case ItemTypeGroup.Clothing   => items.Clothing(binary, brandItem)
      case _                        => throw new IllegalArgumentException("This item type is not yet implemented")
    }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(memory: Memory) =
    itemTypeGroupOf(memory.underlying) match {
      case ItemTypeGroup.MensTShirt => new items.MensTShirt(memory)
      case ItemTypeGroup.Clothing   => new items.Clothing(memory)
      case _                        => throw new IllegalArgumentException("This item type is not yet implemented")
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
  def itemTypeGroupOf(binary: Array[Byte]): ItemTypeGroup =
    ItemTypeGroup(UNSAFE.getInt(binary, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.ITEM_TYPE_GROUP_CORE_OFFSET_BYTES))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def itemTypeGroupOf(memory: Memory): ItemTypeGroup =
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
    (itemTypeGroupOf(binary) >> itemTypeGroupOf(to.underlying))
     // &&
    // (itemTypeOf(binary) eq itemTypeOf(to))
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def brandBinary(item: CatalogueItem): Array[Byte] = item.memory match {
    case p: PrimaryMemory   => p.underlying
    case s: SecondaryMemory => s.primary.underlying
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def storeBinary(item: CatalogueItem): Option[Array[Byte]] = item.memory match {
    case _: PrimaryMemory   => none
    case s: SecondaryMemory => s.underlying.some
  }

  def binary(item: CatalogueItem, owty: OwnerType): Option[Array[Byte]] = owty match {
    case STORE => storeBinary(item)
    case BRAND => brandBinary(item).some
  }

}