package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class StylingTips(text: String) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder): Unit =
    builder.putString(text)
}

object StylingTips extends VariableSizeAttributeConstants {
  def read(prepared: PreparedMemory) = StylingTips(prepared.getString())
}