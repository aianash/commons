package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Description(text: String) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder): Unit =
    builder.putString(text)
}

object Description extends VariableSizeAttributeConstants {
  def read(prepared: PreparedMemory) = Description(prepared.getString())
}