package commons.catalogue.attributes

case class ApparelFabric(fabric: String) extends StringAttribute(fabric)

object ApparelFabric extends StringAttributeConstants[ApparelFabric] {
  def instantiate(value: String) = ApparelFabric(value)
}