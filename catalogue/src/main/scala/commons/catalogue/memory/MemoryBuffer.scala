package commons.catalogue.memory

import java.util.ArrayList

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

import commons.core.util.UnsafeUtil
import commons.catalogue.CatalogueItem
import commons.catalogue.attributes.{Attribute, FixedSizeAttribute, VariableSizeAttribute}


/** A mutable Memory, non-thread safe, which is used to create
  * [[commons.catalogue.memory.Memory]] for immutable [[]]
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
private[catalogue] abstract class MemoryBuffer(numSegments: Int) {

  import UnsafeUtil._
  import MemoryBuffer._
  import Memory._

  private val headerSize = (SHORT_SIZE_BYTES +                          // number of segments
                           (numSegments << POINTER_SIZE_EXP) +          // pointers (offset) for each segments
                           CatalogueItem.CORE_ATTRIBUTES_SIZE_BYTES +   // core attributes
                           7) & ~7                                      // padded to multiple of words (8 bytes)

  // Header contains
  // - number of segments (Short)
  // - offset of each segment in the underlying array
  // - core attributes as defined by catalogue items (all are fized size)
  //
  // Header resides at the beginning of underlying array
  // before any segment starts
  private[this] val header = Array.ofDim[Byte](headerSize)

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

  // Head layout for the current segment
  protected[this] var headLayout: MemoryBuffer.HeadLayout = null

  /** Returns the [[Memory]] instance with segments
    * created in this buffer
    *
    * @return [[Memory]] instance
    */
  def memory(): Memory

  /** Writes byte array to header at offset
    *
    * @param offset   offset in header to write to
    * @param value    byte array
    */
  def writeToHeader(offset: Long, value: Array[Byte]) {
    unsafe.copyMemory(value, BYTE_ARRAY_BASE_OFFSET, header, BYTE_ARRAY_BASE_OFFSET + offset, value.length)
  }

  ////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////// MEMORY SEGMENT METHODS /////////////////////////
  ////////////////////////////////////////////////////////////////////////////////


  /** Creates a new segment to write attributes
    *
    * Segments should be created in the order of hierachy
    * of sub classes of [[CatalogueItem]] starting from top
    *
    * @param headLayout   head layout for the segment
    */
  def begin(headLayout: MemoryBuffer.HeadLayout)

  /** Writes fixed size attribute to the current segment.
    *
    * @param attribute  fixed size attribute
    * @param attrIdx    attribute index in the head
    */
  def writeAttr(attribute: FixedSizeAttribute, attrIdx: Int)

  /** Writes variable size attribute to the current segment.
    *
    * @param attribute  variable size attribute
    * @param attrIdx    attribute index in the head
    */
  def writeAttr(attribute: VariableSizeAttribute, attrIdx: Int)

  /** Ends a segment (called after writing all the attributes). This
    * also updates the segment table with offset in the header.
    */
  def end() {
    val sgmtTblOffset = BYTE_ARRAY_BASE_OFFSET +
                        CatalogueItem.CORE_ATTRIBUTES_SIZE_BYTES +
                        SHORT_SIZE_BYTES +
                        (segmentIdx << POINTER_SIZE_EXP)
    unsafe.putInt(header, sgmtTblOffset, udrlygNxtSgmtOff)

    val segmentSize = (pos - 1 + 7) & ~7 // pos - 1 since pos points
                                         // to the empty byte.
                                         // word align size
    segmentSizes(segmentIdx) = segmentSize
    segments(segmentIdx) = segment

    udrlygNxtSgmtOff += segmentSize
    segment = null
    pos = -1
    headLayout = null
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
  protected def mkBasicSegment(headLayout: MemoryBuffer.HeadLayout, headSizeInBytes: Int) {
    segmentIdx += 1
    pos = (headSizeInBytes + 7) & ~7
    segment = Array.ofDim[Byte](pos + 8) // atleast one more word than pos
                                         // and pos points to the start of it
    this.headLayout = headLayout
  }


  ///////////////////////////////////////////////////////////////////////////////
  ////////////////////////////// PUT METHODS ////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////

  /** Absolute Put i.e. put at given pos in underlying byte array
    * There methods doesnot ensure capacity, hence are unsafe
    * To be used only for writing in head section of segment.
    */

  def putIntAt(pos: Int, value: Int) {}
  def putCharAt(pos: Int, value: Char) {}
  def putLongAt(pos: Int, value: Long) {}
  def putFloatAt(pos: Int, value: Float) {}

  /** Relative Put i.e. put at current pos and move it automatically
    * These methods ensure capacity before writing.
    */

  def putInt(value: Int) {}
  def putString(str: String) {}

  def putFixedSizeElementArray(arr: Array[_], size: Int) {}

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

  def getIntAt(pos: Int) = {1}
  def getLongAt(pos: Int) = {1L}
  def getCharAt(pos: Int) = {'B'}

  def getInt() = {1}
  def getLong() = {1L}


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
    unsafe.copyMemory(header, BYTE_ARRAY_BASE_OFFSET, underlying, BYTE_ARRAY_BASE_OFFSET, header.size)
    var segmentOff = header.size
    for(i <- 0 until segments.size) {
      unsafe.copyMemory(segments(i), BYTE_ARRAY_BASE_OFFSET, underlying, BYTE_ARRAY_BASE_OFFSET + segmentOff, segmentSizes(i))
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
    unsafe.copyMemory(segment, BYTE_ARRAY_BASE_OFFSET, newSegment, BYTE_ARRAY_BASE_OFFSET, segment.length)
    segment = newSegment
  }

}


object MemoryBuffer {

  import UnsafeUtil._
  import Memory._

  /** HeadLayout for a segment
    *
    * @param sizeInBytes 
    * @param attributesInfo
    */
  case class HeadLayout(private val sizeInBytes: Int, private val attributesInfo: ((Int, Class[_ <: Attribute]), Int)*) {

    val NUM_ATTRIBUTES       = attributesInfo.size
    val SECONDARY_SIZE_BYTES = INHERITANCE_BITS_SIZE_BYTES + (NUM_ATTRIBUTES << POINTER_SIZE_EXP)
    val PRIMARY_SIZE_BYTES   = (sizeInBytes + 7) & ~7

    private[this] val idx2Offset = Array.ofDim[Int](attributesInfo.size)
    private[this] val attr2Indx  = new Object2IntOpenHashMap[Class[_ <: Attribute]](attributesInfo.size)

    for(info <- attributesInfo) {
      idx2Offset(info._1._1) = info._2
      attr2Indx.put(info._1._2, info._1._1)
    }

    /**
     * [idx description]
     * @type {[type]}
     */
    def primaryOffsetFor(idx: Int) = idx2Offset(idx)

    /**
     * [clazz description]
     * @type {[type]}
     */
    def indexFor(clazz: Class[_ <: Attribute]) = attr2Indx.get(clazz)

  }

}