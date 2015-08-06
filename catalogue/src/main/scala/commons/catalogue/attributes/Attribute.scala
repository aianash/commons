package commons.catalogue.attributes

import commons.catalogue.memory.{MemoryBuffer, Memory}


/**
 * Top most class which all Attributes should inherit
 * Exception are - OwnerId, CatalogueItemId, VariantId, ItemType
 * and ItemTypeGroups which dont inherit this.
 *
 * Inherting class should define two functions for writing
 * to Primary and Secondary memory
 */
trait Attribute


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
trait FixedSizeAttribute extends Attribute {

  val sizeInBytes: Int

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] def writeAt(buffer: MemoryBuffer, position: Int): Unit

}

/** 
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
trait VariableSizeAttribute extends Attribute {


  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] def write(buffer: MemoryBuffer): Unit

}


trait VariableSizeAttributeConstants {
  private[catalogue] val HEAD_SIZE_BYTES = Memory.POINTER_SIZE_BYTES
}

trait FixedSizeAttributeConstants {
  private[catalogue] val HEAD_SIZE_BYTES: Int
}