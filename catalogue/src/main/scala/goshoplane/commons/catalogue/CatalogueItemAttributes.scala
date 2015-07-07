package goshoplane.commons.catalogue

case class Colors(values: Seq[String])
case class Sizes(values: Seq[String])
case class Brand(name: String)
case class Description(text: String)
case class ItemTypeGroups(groups: Seq[ItemTypeGroup.Value])
case class NamedType(name: String)
case class Price(value: Float)
case class ProductTitle(title: String)
case class ProductImage(small: String, medium: String, large: String)
case class ApparelFit(fit: String)
case class ApparelStyle(style: String)
case class ApparelFabric(fabric: String)

object ItemTypeGroup extends Enumeration {
  type ItemTypeGroup = Value
  val Clothing, MenClothing, Jeans = Value
}