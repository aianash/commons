package commons.catalogue.attributes


object ItemTypeGroup extends Enumeration {

  case class ItemTypeGroup(i: Int, name: String) extends Val(i, name) {}

  val Clothing           = ItemTypeGroup(0, "Clothing")
  val MensClothing       = ItemTypeGroup(1, "MensClothing")
  val MensTShirt         = ItemTypeGroup(2, "MensTShirt")
  val MensPoloNeckTShirt = ItemTypeGroup(3, "MensPoloNeckTShirt")
}


case class ItemTypeGroups(groups: Seq[ItemTypeGroup.ItemTypeGroup])