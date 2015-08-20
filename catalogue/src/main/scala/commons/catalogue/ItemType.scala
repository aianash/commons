package commons.catalogue

import scala.reflect.NameTransformer._

import commons.core.util.UnsafeUtil.INT_SIZE_BYTES

import scala.util.matching.Regex


trait ItemType {
  def id: Int
  def groups: Seq[ItemType]
}

object ItemType {

  val SIZE_BYTES = INT_SIZE_BYTES

  private val vmap = collection.mutable.Map[Int, ItemType]()
  private val nmap = collection.mutable.Map[String, ItemType]()

  val Clothing = new Clothing
  val MensClothing = new MensClothing
  val MensTShirt = new MensTShirt
  val MensPoloNeckTShirt = new MensPoloNeckTShirt

  def apply(id: Int) = vmap(id)
  def apply(name: String) = nmap(name)

  abstract class ItemTypeVal extends ItemType {

    override def toString: String =
      ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
        Regex.quote(NAME_JOIN_STRING)).last

    assert(!vmap.isDefinedAt(id), "Duplicate id: " + id)
    vmap(id) = this
    nmap(toString) = this

    protected def _groups = IndexedSeq.empty[ItemType]

    val groups: Seq[ItemType] = _groups
  }

}

private[catalogue] class Clothing extends ItemType.ItemTypeVal {
  def id = 0
  override def _groups =
    super._groups :+ (if(ItemType.Clothing != null) ItemType.Clothing else this)
}

private[catalogue] class MensClothing extends Clothing {
  override def id = 1
  override def _groups =
    super._groups :+ (if(ItemType.MensClothing != null) ItemType.MensClothing else this)
}

private[catalogue] class MensTShirt extends MensClothing {
  override def id = 2
  override def _groups =
    super._groups :+ (if(ItemType.MensTShirt != null) ItemType.MensTShirt else this)
}

private[catalogue] class MensPoloNeckTShirt extends MensTShirt {
  override def id = 3
  override def _groups =
    super._groups :+ (if(ItemType.MensPoloNeckTShirt != null) ItemType.MensPoloNeckTShirt else this)
}