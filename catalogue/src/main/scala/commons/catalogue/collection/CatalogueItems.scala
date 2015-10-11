package commons
package catalogue
package collection

import scala.collection.JavaConversions._

import core.util.UnsafeUtil
import memory._
import owner._

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
class CatalogueItems(val items: Seq[CatalogueItem]) {

  import UnsafeUtil._
  import CatalogueItem._

  require(items.forall(_.ownerType equals STORE),
    new IllegalArgumentException("All items in provided parameter for items should be of type store"))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def toBinary: Array[Byte] = {
    val idToPrimary = new Long2ObjectOpenHashMap[PrimaryMemory]()
    var secondarySize = 0
    items foreach { item =>
      secondarySize += INT_SIZE_BYTES + item.memory.underlying.size
      val cuid = item.itemId.cuid
      val primary = idToPrimary.get(cuid)
      if(primary == null || itemTypeGroupOf(primary.underlying) >> item.itemTypeGroup)
        idToPrimary.put(cuid, item.memory.asInstanceOf[SecondaryMemory].primary)
    }

    val primarySize = idToPrimary.values.foldLeft(0)(_ + _.underlying.size) + (idToPrimary.size * INT_SIZE_BYTES)
    val totalSize = INT_SIZE_BYTES + primarySize + secondarySize
    val binary = Array.ofDim[Byte](totalSize)

    var pos = BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES

    UNSAFE.putInt(binary, BYTE_ARRAY_BASE_OFFSET, INT_SIZE_BYTES + primarySize)

    idToPrimary.values foreach { memory =>
      val memSize = memory.underlying.size
      UNSAFE.putInt(binary, pos, memSize)
      pos += INT_SIZE_BYTES
      UNSAFE.copyMemory(memory.underlying, BYTE_ARRAY_BASE_OFFSET, binary, pos, memSize)
      pos += memSize
    }

    items foreach { item =>
      val memSize = item.memory.underlying.size
      UNSAFE.putInt(binary, pos, memSize)
      pos += INT_SIZE_BYTES
      UNSAFE.copyMemory(item.memory.underlying, BYTE_ARRAY_BASE_OFFSET, binary, pos, memSize)
      pos += memSize
    }

    binary
  }

}

/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object CatalogueItems {

  import UnsafeUtil._
  import CatalogueItem._

  def apply(items: Seq[CatalogueItem]) = new CatalogueItems(items)

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def apply(binary: Array[Byte]): CatalogueItems = {
    val secondaryOffset = UNSAFE.getInt(binary, BYTE_ARRAY_BASE_OFFSET) + BYTE_ARRAY_BASE_OFFSET

    var pos = BYTE_ARRAY_BASE_OFFSET + INT_SIZE_BYTES

    val idToPrimary = new Long2ObjectOpenHashMap[CatalogueItem]()

    while(pos != secondaryOffset) {
      val memSize = UNSAFE.getInt(binary, pos)
      pos += INT_SIZE_BYTES
      val primaryBinary = Array.ofDim[Byte](memSize)
      UNSAFE.copyMemory(binary, pos, primaryBinary, BYTE_ARRAY_BASE_OFFSET, memSize)
      pos += memSize
      idToPrimary.put(itemIdOf(primaryBinary).cuid, CatalogueItem(primaryBinary))
    }

    var items = Seq.empty[CatalogueItem]

    while(pos != binary.size + BYTE_ARRAY_BASE_OFFSET) {
      val memSize = UNSAFE.getInt(binary, pos)
      pos += INT_SIZE_BYTES
      val secondaryBinary = Array.ofDim[Byte](memSize)
      UNSAFE.copyMemory(binary, pos, secondaryBinary, BYTE_ARRAY_BASE_OFFSET, memSize)
      pos += memSize
      items = items :+ CatalogueItem(secondaryBinary, idToPrimary.get(itemIdOf(secondaryBinary).cuid))
    }

    CatalogueItems(items)
  }

}