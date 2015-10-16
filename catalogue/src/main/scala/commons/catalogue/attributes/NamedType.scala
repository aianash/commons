package commons.catalogue.attributes

case class NamedType(name: String) extends StringAttribute(name)

object NamedType extends StringAttributeConstants[NamedType] {
  def instantiate(value: String) = NamedType(value)
}