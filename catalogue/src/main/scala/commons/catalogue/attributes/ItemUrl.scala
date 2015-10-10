package commons
package catalogue
package attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory

case class ItemUrl(url: String) extends {
  val sizeInBytes = ItemUrl.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putString(url)
  }

}

object ItemUrl extends VariableSizeAttributeConstants {
  def read(prepared: PreparedMemory) =
    ItemUrl(prepared.getString)
}