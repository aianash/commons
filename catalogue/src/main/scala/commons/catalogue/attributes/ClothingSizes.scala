package commons.catalogue.attributes

import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


class ClothingSize(val name: String) extends java.io.Serializable {
  val id = stringHash(name.toLowerCase, symmetricSeed)
}

object ClothingSize {

  private val vmap = scala.collection.mutable.Map[Int, ClothingSize]()
  private val nmap = scala.collection.mutable.Map[String, ClothingSize]()

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name.toLowerCase)

  def sizes = vmap.keySet

  def ++(name: String) = {
    val s = new ClothingSize(name)
    assert(!vmap.isDefinedAt(s.id), "Duplicate id: " + s.id)
    vmap(s.id) = s
    nmap(s.name.toLowerCase) = s
    s
  }

  val S   = ++("S")
  val M   = ++("M")
  val L   = ++("L")
  val XL  = ++("XL")
  val XXL = ++("XXL")
  val XS  = ++("XS")
  val X2S = ++("2XS")
  val X3S = ++("3XS")
  val X2L = ++("2XL")
  val X3L = ++("3XL")

}


case class ClothingSizes(values: Seq[ClothingSize]) extends VariableSizeAttribute {

  override private[catalogue] def write(builder: MemoryBuilder): Unit = {
    builder.putIntCollection(values.map(_.id))
  }

}

object ClothingSizes extends VariableSizeAttributeConstants {

  def read(prepared: PreparedMemory) =
    ClothingSizes(prepared.getIntCollection[Seq].map(ClothingSize(_)))

}