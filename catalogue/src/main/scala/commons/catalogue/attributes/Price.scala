package commons.catalogue.attributes

import commons.core.util.UnsafeUtil
import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


case class Price(value: Float) extends {
  val sizeInBytes = Price.HEAD_SIZE_BYTES
} with FixedSizeAttribute {

  override private[catalogue] def writeAt(builder: MemoryBuilder, position: Int) {
    builder.putFloatAt(position, value)
  }

}

object Price extends FixedSizeAttributeConstants {
  override private[catalogue] val HEAD_SIZE_BYTES = UnsafeUtil.FLOAT_SIZE_BYTES
}