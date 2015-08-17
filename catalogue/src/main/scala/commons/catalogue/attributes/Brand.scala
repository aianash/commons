package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Brand(name: String) extends VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putString(name)
  }

}

object Brand extends VariableSizeAttributeConstants