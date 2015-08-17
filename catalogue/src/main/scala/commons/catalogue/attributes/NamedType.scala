package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory

case class NamedType(name: String) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putString(name)
  }
}


object NamedType extends VariableSizeAttributeConstants {

  @inline def read(memory: PreparedMemory) = {
    NamedType(memory.getString())
  }

}