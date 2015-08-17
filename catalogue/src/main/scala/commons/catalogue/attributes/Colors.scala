package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


object Color extends Enumeration {

  case class Color(i: Int, name: String) extends Val(i, name)

  val RED = Color(0, "Red")
}

case class Colors(values: Seq[Color.Color]) extends {
  val sizeInBytes = Colors.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder) {

  }

}

object Colors extends VariableSizeAttributeConstants