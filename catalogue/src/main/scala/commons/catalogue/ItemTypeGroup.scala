package commons.catalogue

import scala.reflect.NameTransformer._
import scala.util.matching.Regex
import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.core.util.UnsafeUtil.INT_SIZE_BYTES


trait ItemTypeGroup {
  def name: String
  val id = stringHash(name.toLowerCase, symmetricSeed)
  def groups: Seq[ItemTypeGroup]
  def isLeaf: Boolean
  def leaf: this.type

  /** See if this type is an ancestor for the given type
    * (basically looks into the groups hierarchy)
    * @param itemType - given item type to check
    */
  def >>(itemType: ItemTypeGroup) = itemType.groups.exists(_ equals this)

  def <<(itemType: ItemTypeGroup) = itemType >> this

}

object ItemTypeGroup {

  val SIZE_BYTES = INT_SIZE_BYTES

  private val vmap = scala.collection.mutable.Map[Int, ItemTypeGroup]()
  private val nmap = scala.collection.mutable.Map[String, ItemTypeGroup]()
  private val _leaves = scala.collection.mutable.Set.empty[ItemTypeGroup]

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name.toLowerCase)

  def leaves: Seq[ItemTypeGroup] = _leaves.toSeq

  case class ItemTypeGroupVal(name: String, parent: ItemTypeGroup = null) extends ItemTypeGroup {

    assert(parent == null || !parent.isLeaf)
    assert(!vmap.isDefinedAt(id), "Duplicate id: " + id)

    vmap(id) = this
    nmap(name.toLowerCase) = this

    val groups: Seq[ItemTypeGroup] = if(parent != null) parent.groups :+ this else IndexedSeq(this)
    private var _isLeaf = false

    def leaf = {
      _leaves += this
      _isLeaf = true
      this
    }

    def isLeaf = _isLeaf

    override def toString: String = name

  }

  implicit class CreateInstance(name: String) {
    def ~>(g: ItemTypeGroup): ItemTypeGroup = ItemTypeGroupVal(name, g)
  }


  val Clothing       = "Clothing" ~> null
  val MensClothing   = "MensClothing" ~> Clothing
  val MensTShirt     = "MensTShirt" ~> MensClothing leaf

  val WomensClothing = "WomensClothing" ~> Clothing
  val WomensTops     = "WomensTops" ~> WomensClothing leaf
}