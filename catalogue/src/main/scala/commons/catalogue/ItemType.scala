package commons.catalogue

import commons.core.util.UnsafeUtil.INT_SIZE_BYTES


object ItemType extends Enumeration {

  case class ItemType(code: Int, name: String) extends Val(code, name)

  val MensPoloNeckTShirt  = ItemType(0, "MensPoloNeckTShirt")
  val MensRoundNeckTShirt = ItemType(1, "MensRoundNeckTShirt")

  val SIZE_BYTES = INT_SIZE_BYTES

  def withCode(code: Int) = {
    code match {
      case MensPoloNeckTShirt.`code` => MensPoloNeckTShirt
      case MensRoundNeckTShirt.`code` => MensRoundNeckTShirt
    }
  }

}