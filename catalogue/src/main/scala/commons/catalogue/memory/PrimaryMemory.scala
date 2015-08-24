package commons.catalogue.memory

import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class PrimaryMemory(_underlying: Array[Byte]) extends Memory(_underlying) {

  import Memory._

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def isPrimary = true

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val sgmtOffset = segmentOffset(segmentIdx)
    val pos = getPositionAt(sgmtOffset + primaryOffset)
    new PreparedMemory(underlying, sgmtOffset + pos, this)
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    val pos = segmentOffset(segmentIdx) + primaryOffset
    new PreparedMemory(underlying, pos, this)
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def truncateTo(segmentIdx: Int) =
    new PrimaryMemory(newUnderlyingTruncatedTo(segmentIdx))

}


/** Companion object
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object PrimaryMemory {

  /** Creates PrimaryMemory instance using binary array
    *
    * @param binary  Encoded array of bytes
    * @return PrimaryMemory instance
    */
  def apply(binary: Array[Byte]) = new PrimaryMemory(binary)

}