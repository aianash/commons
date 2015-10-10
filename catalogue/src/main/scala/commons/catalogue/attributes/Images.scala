package commons
package catalogue
package attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Images(primary: String, alt: Seq[String]) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putStringCollection(primary +: alt)
  }
}

object Images extends VariableSizeAttributeConstants {
  def read(prepared: PreparedMemory) = {
    val images = prepared.getStringCollection[Seq]
    Images(images.head, images.tail)
  }
}