package commons.catalogue

import scala.reflect.NameTransformer._
import scala.util.matching.Regex
import scala.util.hashing.MurmurHash3.{stringHash, symmetricSeed}

import commons.core.util.UnsafeUtil.INT_SIZE_BYTES


trait ItemTypeGroup {
  val id = stringHash(this.getClass.getName, symmetricSeed)
  def groups: Seq[ItemTypeGroup]

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

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  abstract class WithParent(parent: ItemTypeGroup = null) extends ItemTypeGroup {

    override def toString: String =
      ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
        Regex.quote(NAME_JOIN_STRING)).last

    assert(!vmap.isDefinedAt(id), "Duplicate id: " + id)
    vmap(id) = this
    nmap(toString) = this

    val groups: Seq[ItemTypeGroup] = if(parent != null) parent.groups :+ this else IndexedSeq(this)
  }

  object Clothing extends WithParent(null)
  object MensClothing extends WithParent(Clothing)
  object MensTShirt extends WithParent(MensClothing)

  object WomensClothing extends WithParent(Clothing)
  object WomensTops extends WithParent(WomensClothing)
}