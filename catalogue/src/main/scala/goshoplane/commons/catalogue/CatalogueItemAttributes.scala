package goshoplane.commons.catalogue

case class Colors(values: Seq[String])
case class Sizes(values: Seq[String])
case class Brand(name: String)
case class Description(text: String)
case class ItemTypeGroups(groups: Seq[ItemTypeGroup.Value])
case class NamedType(name: String)
case class Price(value: Float)
case class ProductTitle(title: String)


object ItemTypeGroup extends Enumeration {
  type ItemTypeGroup = Value
  val Clothing, MenClothing, Jeans = Value
}