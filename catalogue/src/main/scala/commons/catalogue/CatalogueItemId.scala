package commons.catalogue

case class CatalogueItemId(cuid: Long) extends AnyVal

object CatalogueItemId {
  val SIZE_BYTES = 8

  val NULL = CatalogueItemId(-1L)
}