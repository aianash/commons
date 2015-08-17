package commons.catalogue.memory.builder

import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}
import commons.catalogue.memory.{Memory, PrimaryMemory}


/** Buffer for Primary Memory
  * @inheritdoc
  *
  * @constructor Create a new instance of Primary Memory Buffer
  * with the given number of segments
  */
private[catalogue] class PrimaryMemoryBuilder(numSegments: Int)
  extends MemoryBuilder(numSegments) {

  /** @inheritdoc
    *
    * @param headLayout   head layout for the segment
    */
  def begin(numAttrs: Int, primaryHeadSize: Int) {
    mkBasicSegment(primaryHeadSize)
  }

  /** @inheritdoc
    *
    * @note Cannot pass null for attribute
    */
  def writeAttr(attribute: VariableSizeAttribute, attrIdx: Int, primaryOffset: Int) {
    if(attribute != null) { // write attribute to this segment
      // 1. word align current segment's pos
      // 2. calcualte offset as in underlying array and put in head section
      // 3. write the attribute at the current pos
      pos = (pos + 7) & ~7
      putIntAt(primaryOffset, udrlygNxtSgmtOff + pos)
      attribute.write(this)
    } else throw new IllegalArgumentException("Variable attribute cannot be null in PrimaryMemory")
  }

  /** @inheritdoc
    *
    * @note Cannot pass null for attribute
    */
  def writeAttr(attribute: FixedSizeAttribute, attrIdx: Int, primaryOffset: Int) {
    if(attribute != null) {
      attribute.writeAt(this, primaryOffset)
    } else throw new IllegalArgumentException("Fixed Size attribute cannot be null in PrimaryMemory")
  }

  /** @inheritdoc
    */
  def memory(): Memory = new PrimaryMemory(mkUnderlying())

}