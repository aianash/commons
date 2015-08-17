package commons.catalogue.memory


private[catalogue] class PreparedMemory(_underlying: Array[Byte], pos: Int, parent: Memory) extends Memory(_underlying) {

  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory =
    parent.prepareForVariable(segmentIdx, attrIdx, primaryOffset)

  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory =
    parent.prepareForFixed(segmentIdx, attrIdx, primaryOffset)

  def getInt(): Int = ???
  def getLong(): Long = ???
  def getString(): String = ???

}