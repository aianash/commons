package commons.catalogue

import commons.core.util.UnsafeUtil

sealed trait OwnerType {
  def code: Char
}
case object STORE extends OwnerType {
  val code = 'S'
}
case object BRAND extends OwnerType {
  val code = 'B'
}

object OwnerType {
  val SIZE_BYTES = UnsafeUtil.CHAR_SIZE_BYTES

  def apply(code: Char) =
    code match {
      case 'S' => STORE
      case 'B' => BRAND
    }

}

trait OwnerId {
  def ownerType: OwnerType
  def owuid: Long
}

case class BrandId(bruid: Long) extends OwnerId {
  def ownerType = BRAND
  def owuid = bruid
}

case class StoreId(stuid: Long) extends OwnerId {
  def ownerType = STORE
  def owuid = stuid
}

object OwnerId {
  val SIZE_BYTES = OwnerType.SIZE_BYTES + UnsafeUtil.INT_SIZE_BYTES

  def apply(ownerType: OwnerType, owuid: Long) = {
    ownerType match {
      case STORE => StoreId(owuid)
      case BRAND => BrandId(owuid)
    }
  }

}