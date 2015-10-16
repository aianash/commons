package commons.catalogue.attributes

case class ApparelFit(fit: String) extends StringAttribute(fit)

object ApparelFit extends StringAttributeConstants[ApparelFit] {
  def instantiate(value: String) = ApparelFit(value)
}