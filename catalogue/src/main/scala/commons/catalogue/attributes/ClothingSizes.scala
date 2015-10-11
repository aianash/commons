package commons.catalogue.attributes

import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


class ClothingSize(val name: String) {
  val id = stringHash(name, symmetricSeed)
}

object ClothingSize {

  private val vmap = scala.collection.mutable.Map[Int, ClothingSize]()
  private val nmap = scala.collection.mutable.Map[String, ClothingSize]()

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  def ++(name: String) = {
    val s = new ClothingSize(name)
    assert(!vmap.isDefinedAt(s.id), "Duplicate id: " + s.id)
    vmap(s.id) = s
    nmap(s.name) = s
    s
  }

  val S   = ++("S")
  val M   = ++("M")
  val L   = ++("L")
  val XL  = ++("XL")
  val XXL = ++("XXL")

}


case class ClothingSizes(values: Seq[ClothingSize]) extends {
  val sizeInBytes = ClothingSizes.HEAD_SIZE_BYTES
} with VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder): Unit = {
    builder.putIntCollection(values.map(_.id))
  }

}

object ClothingSizes extends VariableSizeAttributeConstants {

  def read(prepared: PreparedMemory) =
    ClothingSizes(prepared.getIntCollection[Seq].map(ClothingSize(_)))

}