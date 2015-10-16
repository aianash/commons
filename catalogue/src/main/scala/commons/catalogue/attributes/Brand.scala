package commons.catalogue.attributes

case class Brand(name: String) extends StringAttribute(name)

object Brand extends StringAttributeConstants[Brand] {
  def instantiate(value: String) = Brand(value)
}