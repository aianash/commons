package commons.catalogue.memory

import commons.catalogue.attributes.{FixedSizeAttribute, VariableSizeAttribute}

/** Buffer for Secondary Memory
  * @inheritdoc
  *
  * @constructor Create a new instance of Secondary memory buffer
  * from primary memory and number of segments
  *
  * @param primary    primary memory to inherit instance from
  */
private[catalogue] class SecondaryMemoryBuffer(primary: PrimaryMemory, numSegments: Int)
  extends MemoryBuffer(numSegments) {

  import Memory._

  /** @inheritdoc
    * Also init inheritance bit with zero
    *
    * @param headLayout   head layout for the segment
    */
  def begin(headLayout: MemoryBuffer.HeadLayout) {
    mkBasicSegment(headLayout, headLayout.SECONDARY_SIZE_BYTES)
    putLongAt(0, 0L) // by default all inheritance bits are zero
                     // Note: this may not be necessary
  }

  /** @inheritdoc
    *
    * @Note If attribute is null then it is inherited from the
    * primary memory
    *
    * @param attribute    variable sized attribute
    * @param attrIdx      attribute's index in head layout
    */
  def writeAttr(attribute: VariableSizeAttribute, attrIdx: Int) {
    val attrHeadOffset = INHERITANCE_BITS_SIZE_BYTES +
                         (attrIdx << POINTER_SIZE_EXP)

    if(attribute == null) {
      val primarySegmtOffset = primary.segmentOffset(segmentIdx)
      val attrOffset = primary.getIntAt(primarySegmtOffset + headLayout.primaryOffsetFor(attrIdx))
      putIntAt(attrHeadOffset, attrOffset)
    } else {
      pos = (pos + 7) & ~7
      putIntAt(attrHeadOffset, udrlygNxtSgmtOff + pos)
      attribute.write(this)
      setNotInheritedAt(attrIdx)
    }
  }

  /** @inheritdoc
    * This writed fixed sized attribute in tail section unlike
    * primary memory buffer. Which writed in head section.
    *
    * This is necessary because attributes can be either inherited
    * or overriden. And most of the attributes are always
    * inherited. Hence storing only pointers in head reduces
    * size.
    *
    * @Note If attribute is null then it is inherited from the
    * primary memory
    *
    * @param attribute    fixed sized attribute
    * @param attrIdx      attribute's index in head layout
    */
  def writeAttr(attribute: FixedSizeAttribute, attrIdx: Int) {
    val attrHeadOffset = INHERITANCE_BITS_SIZE_BYTES +
                         (attrIdx << POINTER_SIZE_EXP)

    if(attribute == null) {
      val attrOffset = primary.segmentOffset(segmentIdx) + headLayout.primaryOffsetFor(attrIdx)
      putIntAt(attrHeadOffset, attrOffset)
    } else {
      pos = (pos + 7) & ~7
      val attrSize = attribute.sizeInBytes
      if(pos + attrSize >= segment.length) resize(attrSize)
      attribute.writeAt(this, pos)
      setNotInheritedAt(attrIdx)
      putIntAt(attrHeadOffset, udrlygNxtSgmtOff + pos)
      pos += attrSize
    }
  }

  /** @inheritdoc
    *
    * @return Secondary memory instance
    */
  def memory(): Memory = new SecondaryMemory(primary, mkUnderlying())

  /////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////// PRIVATE METHODS /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////


  /** Sets not inherited in the segment's inheritance bits
    *
    * @param attrIdx    attribute index
    */
  private def setNotInheritedAt(attrIdx: Int) {
    val newBits = (1 << attrIdx) | getLongAt(0)
    putLongAt(0, newBits)
  }

}