package commons.catalogue

import commons.core.util.UnsafeUtil.LONG_SIZE_BYTES


case class VariantId(vrtuid: Long) extends AnyVal

object VariantId {
  val SIZE_BYTES = LONG_SIZE_BYTES

  val NULL = VariantId(-1L)
}