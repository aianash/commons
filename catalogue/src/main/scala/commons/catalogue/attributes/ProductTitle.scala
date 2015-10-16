package commons.catalogue.attributes

case class ProductTitle(title: String) extends StringAttribute(title)

object ProductTitle extends StringAttributeConstants[ProductTitle] {
  def instantiate(value: String) = ProductTitle(value)
}