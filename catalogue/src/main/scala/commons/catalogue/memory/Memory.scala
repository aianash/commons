package commons.catalogue.memory

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

import commons.core.util.UnsafeUtil
import commons.catalogue.attributes.{Attribute, FixedSizeAttribute, VariableSizeAttribute}


/** Memory is used by catalogue item to encode and save its attributes
  * as byte array. This provide high performance encoding for various types
  * usign direct memory manipulation via `sun.misc.Unsafe`.
  *
  * This is also designed to enable attribute inheritance, a mechanism to save
  * memory while processing a huge number of catalogue items. For this there
  * are two types of memory Primary and Secondary, which differs in how they store
  * attributes.
  *
  * Memory is intentionally not thread-safe. And writing attribute requires a
  * specific procedure to be followed.
  *
  * == Underlying Byte Array ==
  * == Memory Segment ==
  * == Memory Hierarchy ==
  *
  */
private[catalogue] abstract class Memory(private[catalogue] val underlying: Array[Byte]) {

  import UnsafeUtil._
  import Memory._
  import builder.MemoryBuilder._

  /** Get segment offset (i.e. starting pos) in the underlying byte array
    *
    * @param idx  Segment index (i.e level in the hierarchy starting from 0)
    */
  private[catalogue] def segmentOffset(idx: Int) =
    unsafe.getInt(underlying, BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES + (idx << POSITION_SIZE_EXP))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareFor(clazz: Class[_ <: Attribute], segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory = {
    if(FIXED_ATTR_CLAZZ isAssignableFrom clazz) prepareForFixed(segmentIdx, attrIdx, primaryOffset)
    else if(VARIABLE_ATTR_CLAZZ isAssignableFrom clazz) prepareForVariable(segmentIdx, attrIdx, primaryOffset)
    else throw new IllegalArgumentException("Attribute's clazz is not either FixedSizeAttribute or VariableSizeAttribute, unknown type")
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForVariable(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory

  /**
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def prepareForFixed(segmentIdx: Int, attrIdx: Int, primaryOffset: Int): PreparedMemory


  ///////////////////////////////////////////////////////////////////////////////
  ////////////////////////////// GET METHODS ////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////

  def getIntAt(pos: Int): Int = ???
  def getLongAt(pos: Int): Long = ???
  def getCharAt(pos: Int): Char = ???
  def getShortAt(pos: Int): Short = ???
  def getStringAt(pos: Int): String = ???
  def getPositionAt(pos: Int): Int = ???
}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object Memory {

  import UnsafeUtil._

  private val VARIABLE_ATTR_CLAZZ = classOf[VariableSizeAttribute]
  private val FIXED_ATTR_CLAZZ = classOf[FixedSizeAttribute]
}