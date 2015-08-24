package commons
package catalogue
package serialization

import akka.serialization._

import commons.core.util.UnsafeUtil
import collection.CatalogueItems
import memory._

class CatalogueItemAkkaSerializer extends Serializer {
  import UnsafeUtil._

  def includeManifest = true

  def identifier = 8080801

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case items: CatalogueItems => items.toBinary
    case item: CatalogueItem => item.memory match {
      case primary: PrimaryMemory =>
        val binary = Array.ofDim[Byte](CHAR_SIZE_BYTES + primary.underlying.size)
        UNSAFE.putChar(binary, BYTE_ARRAY_BASE_OFFSET, 'P')
        UNSAFE.copyMemory(primary.underlying, BYTE_ARRAY_BASE_OFFSET, binary, BYTE_ARRAY_BASE_OFFSET + CHAR_SIZE_BYTES, primary.underlying.size)
        binary
      case secondary: SecondaryMemory =>
        val primarySize = secondary.primary.underlying.size
        val secondarySize = secondary.underlying.size
        val binary = Array.ofDim[Byte](CHAR_SIZE_BYTES + INT_SIZE_BYTES + primarySize + secondarySize)
        var pos = BYTE_ARRAY_BASE_OFFSET
        UNSAFE.putChar(binary, pos, 'S')
        pos += CHAR_SIZE_BYTES
        UNSAFE.putInt(binary, pos, primarySize)
        pos += INT_SIZE_BYTES
        UNSAFE.copyMemory(secondary.primary.underlying, BYTE_ARRAY_BASE_OFFSET, binary, pos, primarySize)
        pos += primarySize
        UNSAFE.copyMemory(secondary.underlying, BYTE_ARRAY_BASE_OFFSET, binary, pos, secondarySize)
        binary
    }
    case _ => throw new IllegalArgumentException("Provided parameter is not of type CatalogueItems")
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def fromBinary(binary: Array[Byte], clazz: Option[Class[_]]): AnyRef =
    if(clazz.get isAssignableFrom classOf[CatalogueItems]) CatalogueItems(binary)
    else if(clazz.get isAssignableFrom classOf[CatalogueItem]) {
      UNSAFE.getChar(binary, BYTE_ARRAY_BASE_OFFSET) match {
        case 'P' =>
          val primaryBytes = Array.ofDim[Byte](binary.size - CHAR_SIZE_BYTES)
          UNSAFE.copyMemory(binary, BYTE_ARRAY_BASE_OFFSET + CHAR_SIZE_BYTES, primaryBytes, BYTE_ARRAY_BASE_OFFSET, primaryBytes.size)
          CatalogueItem(PrimaryMemory(primaryBytes))
        case 'S' =>
          var pos = BYTE_ARRAY_BASE_OFFSET + CHAR_SIZE_BYTES
          val primarySize = UNSAFE.getInt(binary, pos)
          pos += INT_SIZE_BYTES
          val primaryBytes = Array.ofDim[Byte](primarySize)
          UNSAFE.copyMemory(binary, pos, primaryBytes, BYTE_ARRAY_BASE_OFFSET, primarySize)
          pos += primarySize
          val secondarySize = (binary.size - pos + BYTE_ARRAY_BASE_OFFSET).toInt
          val secondaryBytes = Array.ofDim[Byte](secondarySize)
          UNSAFE.copyMemory(binary, pos, secondaryBytes, BYTE_ARRAY_BASE_OFFSET, secondarySize)
          CatalogueItem(SecondaryMemory(secondaryBytes, PrimaryMemory(primaryBytes)))
      }
    } else throw new IllegalArgumentException("Provided parameter bytes array doesnot belong to CatalogueItems or CatalogueItem")

}
