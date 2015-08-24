package commons.catalogue.memory

import commons.core.util.UnsafeUtil
import commons.catalogue.CatalogueItem
import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}
import builder.{MemoryBuilder, SecondaryMemoryBuilder}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class SecondaryMemory(private[catalogue] var primary: PrimaryMemory, _underlying: Array[Byte]) extends Memory(_underlying) {

  import UnsafeUtil._
  import Memory._
  import MemoryBuilder.POSITION_SIZE_EXP
  import SecondaryMemoryBuilder.INHERITANCE_BITS_SIZE_BYTES

  require(canInherit(underlying, from = primary),
    new IllegalArgumentException("argument for PrimaryMemory is not compatible with argument binary for secondary memory"))

  // array index for convenient fetching the right array using
  // inheritance bits
  private val targetMemory = Array.ofDim[Memory](2)
  targetMemory(0) = primary
  targetMemory(1) = this

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def isPrimary = false

  /** @inheritdoc
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val memory = memoryForAttribute(sgmtOffset, attrIdx)
    val position = getPositionAt(offset) + memory.segmentOffset(segmentIdx)
    new PreparedMemory(memory.underlying, position, this)
  }

  /** @inheritdoc
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val memory = memoryForAttribute(sgmtOffset, attrIdx)
    val position = getPositionAt(offset) + memory.segmentOffset(segmentIdx)
    new PreparedMemory(memory.underlying, position, this)
  }

  /** @inheritdoc
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def truncateTo(segmentIdx: Int) =
    new SecondaryMemory(primary, newUnderlyingTruncatedTo(segmentIdx))

  private def memoryForAttribute(segmentOffset: Int, attrIdx: Int) = {
    val bits = getInheritanceBits(segmentOffset)
    val index = (bits >> attrIdx) & 1
    targetMemory(index.toInt)
  }

  private def getInheritanceBits(offset: Int) = getLongAt(offset)

  private def canInherit(binary: Array[Byte], from: PrimaryMemory) = {
    true && CatalogueItem.compatible(binary, to = from)
  }

}

object SecondaryMemory {
  import UnsafeUtil._

  def apply(binary: Array[Byte], memory: Memory): SecondaryMemory = {
    memory match {
      case primary: PrimaryMemory => SecondaryMemory(binary, primary)
      case _ => throw new IllegalArgumentException("Currently secondary memory can inherit only primary memory")
    }
  }

  def apply(binary: Array[Byte], primary: PrimaryMemory) =
    new SecondaryMemory(primary, binary)

}