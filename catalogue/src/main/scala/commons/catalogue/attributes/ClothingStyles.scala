package commons
package catalogue
package attributes

import scala.reflect.NameTransformer._
import scala.util.matching.Regex
import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


class ClothingStyle(val name: String) {
  val id = stringHash(name, symmetricSeed)
}

object ClothingStyle {

  private val vmap = scala.collection.mutable.Map[Int, ClothingStyle]()
  private val nmap = scala.collection.mutable.Map[String, ClothingStyle]()

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  def ++(name: String) = {
    val s = new ClothingStyle(name)
    assert(!vmap.isDefinedAt(s.id), "Duplicate id: " + s.id)
    vmap(s.id) = s
    nmap(s.name) = s
    s
  }

  val TeesTop      = ++("Tees Top")
  val BodysuitTop  = ++("Bodysuit Top")
  val CropTop      = ++("Crop Top")
  val TubeTop      = ++("Tube Top")
  val PeplumTop    = ++("Peplum Top")
  val CowlTop      = ++("Cowl Top")
  val SpaghettiTop = ++("Spaghetti Top")
  val HalterTop    = ++("Halter Top")
  val TunicsTop    = ++("Tunics Top")
  val TanksTop     = ++("Tanks Top")
  val BasicTop     = ++("Basic Top")

}


case class ClothingStyles(styles: Seq[ClothingStyle]) extends {
  val sizeInBytes = ClothingStyles.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder): Unit = {
    builder.putIntCollection(styles.map(_.id))
  }

}

object ClothingStyles extends VariableSizeAttributeConstants {

  def read(prepared: PreparedMemory) =
    ClothingStyles(prepared.getIntCollection[Seq].map(ClothingStyle(_)))

}