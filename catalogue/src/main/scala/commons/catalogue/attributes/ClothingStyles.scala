package commons
package catalogue
package attributes

import scala.reflect.NameTransformer._
import scala.util.matching.Regex
import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


trait ClothingStyle {
  val id = stringHash(this.getClass.getName, symmetricSeed)
  val name = ("[A-Z][a-z]*".r findAllIn toString) mkString(" ")
}

object ClothingStyle {

  private val vmap = scala.collection.mutable.Map[Int, ClothingStyle]()
  private val nmap = scala.collection.mutable.Map[String, ClothingStyle]()

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  abstract class RegisterClothingStyle extends ClothingStyle {

    override def toString: String =
      ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
        Regex.quote(NAME_JOIN_STRING)).last

    assert(!vmap.isDefinedAt(id), "Duplicate id: " + id)

    vmap(id) = this
    nmap(name) = this
  }

  object TeesTop extends RegisterClothingStyle
  object BodysuitTop extends RegisterClothingStyle
  object CropTop extends RegisterClothingStyle
  object TubeTop extends RegisterClothingStyle
  object PeplumTop extends RegisterClothingStyle
  object CowlTop extends RegisterClothingStyle
  object SpaghettiTop extends RegisterClothingStyle
  object HalterTop extends RegisterClothingStyle
  object TunicsTop extends RegisterClothingStyle
  object TanksTop extends RegisterClothingStyle
  object BasicTop extends RegisterClothingStyle

}


case class ClothingStyles(styles: Seq[ClothingStyle]) extends {
  val sizeInBytes = ClothingStyles.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder): Unit = {
    // builder.putFixedSizeElementArray(styles.map(_.id))
  }

}

object ClothingStyles extends VariableSizeAttributeConstants {

  def read(prepared: PreparedMemory) = {
    // prepared.readFixedSizeElementArray[Int]
    ClothingStyles(Seq.empty[ClothingStyle]) // implement
  }

}