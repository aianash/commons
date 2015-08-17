package commons.catalogue.attributes

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


object ClothingSize extends Enumeration {
  case class ClothingSize(i: Int, name: String) extends Val(i, name) {}

  val S   = ClothingSize(0, "S")
  val M   = ClothingSize(1, "M")
  val L   = ClothingSize(2, "L")
  val XL  = ClothingSize(3, "XL")
  val XXL = ClothingSize(4, "XXL")
}

case class ClothingSizes(values: Seq[ClothingSize.ClothingSize]) extends {
  val sizeInBytes = ClothingSizes.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder) {

  }

}


object ClothingSizes extends VariableSizeAttributeConstants