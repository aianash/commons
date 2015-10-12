package commons
package catalogue
package attributes

import scala.reflect.NameTransformer._
import scala.util.matching.Regex
import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import java.util.ArrayList

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


class ClothingStyle(val name: String) {
  val id = stringHash(name, symmetricSeed)
}

object ClothingStyle {

  private val vmap = scala.collection.mutable.Map[Int, ClothingStyle]()
  private val nmap = scala.collection.mutable.Map[String, ClothingStyle]()
  private val grpmap = scala.collection.mutable.Map[ClothingStyle, ItemTypeGroup]()

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  def styles = vmap.values

  def group(style: ClothingStyle) = grpmap(style)

  def ++(name: String) = {
    val s = new ClothingStyle(name)
    assert(!vmap.isDefinedAt(s.id), "Duplicate id: " + s.id)
    vmap(s.id) = s
    nmap(s.name) = s
    s
  }

  implicit class AddItemTypeGroup(style: ClothingStyle) {
    def ~>(group: ItemTypeGroup) = {
      grpmap(style) = group
      style
    }
  }

  import ItemTypeGroup._

  val TeesTop      = ++("Tees Top") ~> WomensTops
  val BodysuitTop  = ++("Bodysuit Top") ~> WomensTops
  val CropTop      = ++("Crop Top") ~> WomensTops
  val TubeTop      = ++("Tube Top") ~> WomensTops
  val PeplumTop    = ++("Peplum Top") ~> WomensTops
  val CowlTop      = ++("Cowl Top") ~> WomensTops
  val SpaghettiTop = ++("Spaghetti Top") ~> WomensTops
  val HalterTop    = ++("Halter Top") ~> WomensTops
  val TunicsTop    = ++("Tunics Top") ~> WomensTops
  val TanksTop     = ++("Tanks Top") ~> WomensTops
  val BasicTop     = ++("Basic Top") ~> WomensTops

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