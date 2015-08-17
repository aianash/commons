package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class ProductTitle(title: String) extends VariableSizeAttribute {
  override private[catalogue] def write(builder: MemoryBuilder) {
    builder.putString(title)
  }
}

object ProductTitle extends VariableSizeAttributeConstants {

  def read(prepared: PreparedMemory) = {
    ProductTitle(prepared.getString())
  }

}