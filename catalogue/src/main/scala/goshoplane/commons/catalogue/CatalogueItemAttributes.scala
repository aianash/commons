package goshoplane.commons.catalogue

case class Colors(values: Array[String])
case class Sizes(values: Array[String])
case class Brand(name: String)
case class ClothingType(value: String)
case class Description(text: String)
case class ItemTypeGroups(groups: Array[ItemTypeGroup.Value])
case class NamedType(name: String)
case class Price(value: Float)
case class ProductTitle(title: String)


object ItemTypeGroup extends Enumeration {
  type ItemTypeGroup = Value
  val Clothing, MenClothing, Jeans = Value
}