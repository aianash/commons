package commons.catalogue.memory

import commons.core.util.UnsafeUtil
import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}
import builder.{MemoryBuilder, SecondaryMemoryBuilder}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class SecondaryMemory(private var primary: PrimaryMemory, _underlying: Array[Byte]) extends Memory(_underlying) {

  import UnsafeUtil._
  import Memory._
  import MemoryBuilder.POSITION_SIZE_EXP
  import SecondaryMemoryBuilder.INHERITANCE_BITS_SIZE_BYTES

  // array index for convenient fetching the right array using
  // inheritance bits
  private val targetMemory = Array.ofDim[Memory](2)
  targetMemory(0) = primary
  targetMemory(1) = this

  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val memory = memoryForAttribute(sgmtOffset, attrIdx)
    val position = getPositionAt(offset) + memory.segmentOffset(segmentIdx)
    new PreparedMemory(memory.underlying, position, this)
  }

  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val memory = memoryForAttribute(sgmtOffset, attrIdx)
    val position = getPositionAt(offset) + memory.segmentOffset(segmentIdx)
    new PreparedMemory(memory.underlying, position, this)
  }

  def binary = {
    val binary = Array.ofDim[Byte](INT_SIZE_BYTES + primary.underlying.size + underlying.size)
    UNSAFE.putInt(binary, BYTE_ARRAY_BASE_OFFSET, primary.underlying.size)
    UNSAFE.copyMemory(primary.underlying, BYTE_ARRAY_BASE_OFFSET, binary, BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES, primary.underlying.size)
    UNSAFE.copyMemory(underlying, BYTE_ARRAY_BASE_OFFSET, binary, BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES + primary.underlying.size, underlying.size)
    binary
  }

  private def memoryForAttribute(segmentOffset: Int, attrIdx: Int) = {
    val bits = getInheritanceBits(segmentOffset)
    val index = (bits >> attrIdx) & 1
    targetMemory(index.toInt)
  }

  private def getInheritanceBits(offset: Int) = getLongAt(offset)
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

  def apply(encoded: Array[Byte]): SecondaryMemory = {
    val sizeOfPrimary = UNSAFE.getInt(encoded, BYTE_ARRAY_BASE_OFFSET)
    val primaryArr = Array.ofDim[Byte](sizeOfPrimary)
    val secondaryArr = Array.ofDim[Byte](encoded.size - INT_SIZE_BYTES - sizeOfPrimary)
    UNSAFE.copyMemory(encoded, BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES, primaryArr, BYTE_ARRAY_BASE_OFFSET, sizeOfPrimary)
    UNSAFE.copyMemory(encoded, BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES + sizeOfPrimary, secondaryArr, BYTE_ARRAY_BASE_OFFSET, secondaryArr.size)
    SecondaryMemory(secondaryArr, PrimaryMemory(primaryArr))
  }

}