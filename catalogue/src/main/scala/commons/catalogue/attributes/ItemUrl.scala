package commons
package catalogue
package attributes

case class ItemUrl(url: String) extends StringAttribute(url)

object ItemUrl extends StringAttributeConstants[ItemUrl] {
  def instantiate(value: String) = ItemUrl(value)
}