package commons.catalogue.memory

import scala.collection.generic.CanBuildFrom

import commons.core.util.{UnsafeUtil, FastStringUtils}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] class PreparedMemory(_underlying: Array[Byte], var pos: Int, parent: Memory) extends Memory(_underlying) {

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

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def getStringCollection[M[X] <: TraversableOnce[X]]()(implicit cbf: CanBuildFrom[Nothing, String, M[String]]): M[String] = {
    val bldr = cbf()
    val size = getIntAt(pos)
    pos += INT_SIZE_BYTES

    for(i <- 0 until size) {
      val str = getStringAt(pos)
      bldr += str
      pos += FAST_STRING_IMPLEMENTATION.numBytesRequiredToEncode(str)
    }

    bldr.result()
  }

  def getIntCollection[M[X] <: TraversableOnce[X]]()(implicit cbf: CanBuildFrom[Nothing, Int, M[Int]]): M[Int] = {
    val bldr = cbf()
    val size = getIntAt(pos)
    pos += INT_SIZE_BYTES

    for(i <- 0 until size) {
      val str = getIntAt(pos)
      bldr += str
      pos += INT_SIZE_BYTES
    }

    bldr.result()
  }

  /** @inheritdoc
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def truncateTo(segmentIdx: Int) = parent.truncateTo(segmentIdx)

}