package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


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
  private[catalogue] def writeAt(builder: MemoryBuilder, position: Int): Unit

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
  private[catalogue] def write(builder: MemoryBuilder): Unit

}


trait VariableSizeAttributeConstants {
  private[catalogue] val HEAD_SIZE_BYTES = MemoryBuilder.POSITION_SIZE_BYTES
}

trait FixedSizeAttributeConstants {
  private[catalogue] val HEAD_SIZE_BYTES: Int
}


abstract class StringAttribute(value: String) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder): Unit = builder.putString(value)
}

trait StringAttributeConstants[T] extends VariableSizeAttributeConstants {
  def instantiate(value: String): T
  def read(prepared: PreparedMemory): T = instantiate(prepared.getString())
}