package commons
package catalogue

import core.util.UnsafeUtil


case class CatalogueItemId(cuid: Long) extends AnyVal

object CatalogueItemId {

  val SIZE_BYTES = UnsafeUtil.LONG_SIZE_BYTES

  val NULL = CatalogueItemId(-1L)

}