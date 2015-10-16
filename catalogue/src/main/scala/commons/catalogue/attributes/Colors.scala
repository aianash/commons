package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Colors(values: Seq[String]) extends VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putStringCollection(values)
  }

}

object Colors extends VariableSizeAttributeConstants {
  def read(prepared: PreparedMemory) = Colors(prepared.getStringCollection[Seq])
}