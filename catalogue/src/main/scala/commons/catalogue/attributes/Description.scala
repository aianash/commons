package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Description(text: String) extends StringAttribute(text)

object Description extends StringAttributeConstants[Description] {
  def instantiate(value: String) = Description(value)
}