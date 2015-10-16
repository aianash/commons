package commons.catalogue.attributes

case class StylingTips(text: String) extends StringAttribute(text)

object StylingTips extends StringAttributeConstants[StylingTips] {
  def instantiate(value: String) = StylingTips(value)
}