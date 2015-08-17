package commons.catalogue

case class VariantId(vrtuid: Long) extends AnyVal

object VariantId {
  val SIZE_BYTES = 8

  val NULL = VariantId(-1L)
}