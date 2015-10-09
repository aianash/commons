package commons.catalogue.memory.builder

import java.util.ArrayList

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

import commons.core.util.{UnsafeUtil, FastStringUtils}
import commons.catalogue.CatalogueItem
import commons.catalogue.attributes.{Attribute, FixedSizeAttribute, VariableSizeAttribute}
import commons.catalogue.memory.Memory

/** A mutable Memory, non-thread safe, which is used to create
  * [[commons.catalogue.memory.Memory]] for immutable [[]]
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] abstract class MemoryBuilder(numSegments: Int) {

  import UnsafeUtil._
  import FastStringUtils._
  import MemoryBuilder._
  import Memory._

  private val headerSize = (CatalogueItem.CORE_ATTRIBUTES_SIZE_BYTES +   // core attributes
                           SHORT_SIZE_BYTES +                          // number of segments
                           (numSegments << POSITION_SIZE_EXP) +         // pointers (offset) for each segments
                           7) & ~7                                      // padded to multiple of words (8 bytes)

  // Header contains
  // - core attributes as defined by catalogue items (all are fized size)
  // - number of segments (Short)
  // - offset of each segment in the underlying array
  //
  // size of Header is always in multiple of words (i.e. 8 bytes)
  //
  // Header resides at the beginning of underlying array
  // before any segment starts
  private[this] val header = Array.ofDim[Byte](headerSize)

  // Put number (as Short) of segments in header
  // Short is enough to store segments count (ie. number of segments)
  // because, there wont ever be more than ~65K segments
  UNSAFE.putShort(header, BYTE_ARRAY_BASE_OFFSET + CatalogueItem.CORE_ATTRIBUTES_SIZE_BYTES, numSegments.toShort)

  // Array of segments (Array[Byte]).
  // Any segment once added to this, should be assumed final
  private[this] val segments = Array.ofDim[Array[Byte]](numSegments)

  // Holds the actual size of each segment (this may be different that segment.size)
  private[this] val segmentSizes = Array.ofDim[Int](numSegments)

  // Offset in the underlying array for the next/current segment
  // First segment starts after header.
  // Note: header is already padded to multiple of words
  protected[this] var udrlygNxtSgmtOff = header.size

  // Current segment's index, this is the level in the hierarchy of Catalogue Items
  protected[this] var segmentIdx = -1

  // Current segment for writing attributes
  protected[this] var segment: Array[Byte] = null

  // Current pos in the current segment
  // It alwsys point to the next empty
  // byte
  protected[this] var pos = -1

  /** Returns the [[Memory]] instance with segments
    * created in this buffer
    *
    * @return [[Memory]] instance
    */
  def memory(): Memory

  ////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////// MEMORY SEGMENT METHODS /////////////////////////
  ////////////////////////////////////////////////////////////////////////////////

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def beginHeader() {
    segment = header
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def endHeader() {
    segment = null
  }

  /** Creates a new segment to write attributes
    *
    * Segments should be created in the order of hierachy
    * of sub classes of [[CatalogueItem]] starting from top
    *
    * @param headLayout   head layout for the segment
    */
  def begin(numAttrs: Int, primaryHeadSize: Int)

  /** Writes fixed size attribute to the current segment.
    *
    * @param attribute  fixed size attribute
    * @param attrIdx    attribute index in the head
    */
  def writeAttr(attribute: FixedSizeAttribute, attrIdx: Int, primaryOffset: Int)

  /** Writes variable size attribute to the current segment.
    *
    * @param attribute  variable size attribute
    * @param attrIdx    attribute index in the head
    */
  def writeAttr(attribute: VariableSizeAttribute, attrIdx: Int, primaryOffset: Int)

  /** Ends a segment (called after writing all the attributes). This
    * also updates the segment table with offset in the header.
    */
  def end() {
    val offsetInSgmtTbl = BYTE_ARRAY_BASE_OFFSET +
                          CatalogueItem.CORE_ATTRIBUTES_SIZE_BYTES +
                          SHORT_SIZE_BYTES +
                          (segmentIdx << POSITION_SIZE_EXP)

    UNSAFE.putInt(header, offsetInSgmtTbl, udrlygNxtSgmtOff)

    val segmentSize = (pos - 1 + 7) & ~7 // pos - 1 since pos points
                                         // to the empty byte.
                                         // word align size
    segmentSizes(segmentIdx) = segmentSize
    segments(segmentIdx) = segment

    udrlygNxtSgmtOff += segmentSize
    segment = null
    pos = -1
  }

  /** Creates basic segment for a given head layout and initialize it for
    * writing attributes. In initialization it -
    * - increment the segmentIdx for the current segment
    * - creates byte array for this segment
    * - adjust pos to next empty byte for writing attributes
    * - save head layout to member field
    *
    * @param headLayout       head layout for the segment
    * @param headSizeInBytes  head size in bytes (note: this could be either secondary memory head size or primary memory head size
    */
  protected def mkBasicSegment(headSizeInBytes: Int) {
    segmentIdx += 1
    pos = (headSizeInBytes + 7) & ~8
    segment = Array.ofDim[Byte](pos + 8) // atleast one more word than pos
                                         // and pos points to the start of it
  }


  ///////////////////////////////////////////////////////////////////////////////
  ////////////////////////////// PUT METHODS ////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////

  /** Absolute Put i.e. put at given pos in underlying byte array
    * There methods doesnot ensure capacity, hence are unsafe
    * To be used only for writing in head section of segment.
    */

  def putShortAt(pos: Int, value: Short): Unit =
    UNSAFE.putShort(segment, BYTE_ARRAY_BASE_OFFSET + pos, value)

  def putIntAt(pos: Int, value: Int): Unit =
    UNSAFE.putInt(segment, BYTE_ARRAY_BASE_OFFSET + pos, value)

  def putCharAt(pos: Int, value: Char): Unit =
    UNSAFE.putChar(segment, BYTE_ARRAY_BASE_OFFSET + pos, value)

  def putLongAt(pos: Int, value: Long): Unit =
    UNSAFE.putLong(segment, BYTE_ARRAY_BASE_OFFSET + pos, value)

  def putFloatAt(pos: Int, value: Float): Unit =
    UNSAFE.putFloat(segment, BYTE_ARRAY_BASE_OFFSET + pos, value)

  def putStringAt(pos: Int, value: String) =
    FAST_STRING_IMPLEMENTATION.putString(value, segment, pos)


  /** Relative Put i.e. put at current pos and move it automatically
    * These methods ensure capacity before writing.
    */

  def putInt(value: Int): Unit = {
    if(pos + INT_SIZE_BYTES > segment.size) resize(INT_SIZE_BYTES)
    putIntAt(pos, value)
    pos += INT_SIZE_BYTES
  }

  def putString(str: String) = {
    val atleast = FAST_STRING_IMPLEMENTATION.numBytesRequiredToEncode(str)
    if(pos + atleast > segment.size) resize(atleast)
    putStringAt(pos, str)
    pos += atleast
    atleast
  }

  def putFixedSizeElementArray(arr: Array[_], eachElementSize: Int) {
    val atleast = arr.size * eachElementSize + INT_SIZE_BYTES
    if(pos + atleast > segment.size) resize(atleast)
    putIntAt(pos, arr.size)
    pos += INT_SIZE_BYTES
    
  }

  def putFixedSizeElementArray(arr: Array[_ <: AnyVal]) {
    val elemSize = // optimize this
      arr(0) match {
        case _: Boolean => 1
        case _: Byte    => 1
        case _: Char    => 1
        case _: Short   => 2
        case _: Int     => 4
        case _: Long    => 8
        case _: Float   => 4
        case _: Double  => 8
        case _          => throw new IllegalArgumentException("Dont understand the type of the array passed")
      }

    putFixedSizeElementArray(arr, elemSize)
  }

  ///////////////////////////////////////////////////////////////////////////////
  ////////////////////////////// GET METHODS ////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////

  def getLongAt(pos: Int): Long =
    UNSAFE.getLong(segment, BYTE_ARRAY_BASE_OFFSET + pos)

  /////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////// PRIVATE METHODS /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////


  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  protected def mkUnderlying() = {
    val underlying = Array.ofDim[Byte](udrlygNxtSgmtOff)
    UNSAFE.copyMemory(header, BYTE_ARRAY_BASE_OFFSET, underlying, BYTE_ARRAY_BASE_OFFSET, header.size)
    var segmentOff = header.size
    for(i <- 0 until segments.size) {
      UNSAFE.copyMemory(segments(i), BYTE_ARRAY_BASE_OFFSET, underlying, BYTE_ARRAY_BASE_OFFSET + segmentOff, segmentSizes(i))
      segmentOff += segmentSizes(i)
    }
    underlying
  }

  /** Resize(increase) the current segment with atleast size.
    * Increased size is always in mulitple of blocks
    *
    * @param atleast    increase by atleast this size
    */
  protected def resize(atleast: Int = BLOCK_SIZE_BYTES) {
    val incr = (atleast + BLOCK_SIZE_BYTES_MINUS_ONE) & ~BLOCK_SIZE_BYTES_MINUS_ONE
    val newSegment = Array.ofDim[Byte](segment.length + incr)
    UNSAFE.copyMemory(segment, BYTE_ARRAY_BASE_OFFSET, newSegment, BYTE_ARRAY_BASE_OFFSET, segment.length)
    segment = newSegment
  }

}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object MemoryBuilder {

  import UnsafeUtil._
  import Memory._
  import SecondaryMemoryBuilder._

  // Position offset in the byte array is stored
  // as int type
  private[catalogue] val POSITION_SIZE_EXP   = INT_SIZE_EXP
  private[catalogue] val POSITION_SIZE_BYTES = INT_SIZE_BYTES

  private[catalogue] val BLOCK_SIZE_EXP = 7
  private[catalogue] val BLOCK_SIZE_BYTES = 1 << BLOCK_SIZE_EXP
  private[catalogue] val BLOCK_SIZE_BYTES_MINUS_ONE = BLOCK_SIZE_BYTES - 1

}