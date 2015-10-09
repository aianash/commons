package commons.catalogue.memory

import commons.core.util.{UnsafeUtil, FastStringUtils}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class PreparedMemory(_underlying: Array[Byte], pos: Int, parent: Memory) extends Memory(_underlying) {

  import UnsafeUtil._
  import FastStringUtils._

  def isPrimary = parent.isPrimary

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory =
    parent.prepareForVariable(segmentIdx, attrIdx, primaryOffset)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory =
    parent.prepareForFixed(segmentIdx, attrIdx, primaryOffset)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getShort(): Short =
    UNSAFE.getShort(_underlying, BYTE_ARRAY_BASE_OFFSET + pos)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getInt(): Int =
    UNSAFE.getInt(_underlying, BYTE_ARRAY_BASE_OFFSET + pos)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getLong(): Long =
    UNSAFE.getLong(_underlying, BYTE_ARRAY_BASE_OFFSET + pos)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getFloat(): Float =
    UNSAFE.getFloat(_underlying, BYTE_ARRAY_BASE_OFFSET + pos)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getString(): String =
    FAST_STRING_IMPLEMENTATION.getString(_underlying, pos)

  /** @inheritdoc
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def truncateTo(segmentIdx: Int) = parent.truncateTo(segmentIdx)

}