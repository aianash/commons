package commons.catalogue

import scala.reflect.NameTransformer._
import scala.util.matching.Regex

import commons.core.util.UnsafeUtil

sealed trait OwnerType {
  def code: Char
  def name =
    ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
      Regex.quote(NAME_JOIN_STRING)).last
}

case object STORE extends OwnerType {
  val code = 'S'
}
case object BRAND extends OwnerType {
  val code = 'B'
}

object OwnerType {

  val SIZE_BYTES = UnsafeUtil.CHAR_SIZE_BYTES

  def apply(code: Char) = code match {
    case 'S' => STORE
    case 'B' => BRAND
  }

}

trait OwnerId {

  def ownerType: OwnerType
  def owuid: Long

  override def equals(that: Any) = that match {
    case that: OwnerId =>
      (this eq that) ||
      ((that.ownerType equals this.ownerType) &&
      (that.owuid == this.owuid))
    case _ => false
  }

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
  val SIZE_BYTES = OwnerType.SIZE_BYTES + UnsafeUtil.LONG_SIZE_BYTES

  def apply(ownerType: OwnerType, owuid: Long) = {
    ownerType match {
      case STORE => StoreId(owuid)
      case BRAND => BrandId(owuid)
    }
  }

}