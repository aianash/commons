package commons.catalogue.memory

import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}
import builder.{MemoryBuilder, SecondaryMemoryBuilder}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class SecondaryMemory(private var primary: PrimaryMemory, _underlying: Array[Byte]) extends Memory(_underlying) {

  import Memory._
  import MemoryBuilder.POSITION_SIZE_EXP
  import SecondaryMemoryBuilder.INHERITANCE_BITS_SIZE_BYTES

  // array index for convenient fetching the right array using
  // inheritance bits
  private val underlyingPtr = Array.ofDim[Array[Byte]](2)
  underlyingPtr(0) = primary.underlying
  underlyingPtr(1) = underlying

  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val position = getPositionAt(offset)
    new PreparedMemory(underlyingForAttribute(sgmtOffset, attrIdx), position, this)
  }

  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val offset = sgmtOffset +
                 INHERITANCE_BITS_SIZE_BYTES +
                 (attrIdx << POSITION_SIZE_EXP)
    val position = getPositionAt(offset)
    new PreparedMemory(underlyingForAttribute(sgmtOffset, attrIdx), position, this)
  }

  private def underlyingForAttribute(segmentOffset: Int, attrIdx: Int) = {
    val bits = getInheritanceBits(segmentOffset)
    val index = (bits & (1 << attrIdx)) >> attrIdx
    underlyingPtr(index.toInt)
  }

  private def getInheritanceBits(offset: Int) = getLongAt(offset)
}

object SecondaryMemory {

  def create(binary: Array[Byte], memory: Memory): SecondaryMemory = {
    memory match {
      case primary: PrimaryMemory => create(binary, primary)
      case _ => throw new IllegalArgumentException("Currently secondary memory can inherit only primary memory")
    }
  }

  def create(binary: Array[Byte], primary: PrimaryMemory) =
    new SecondaryMemory(primary, binary)
}