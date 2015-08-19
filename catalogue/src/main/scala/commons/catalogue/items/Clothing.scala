package commons.catalogue.items

import commons.catalogue.CatalogueItem
import commons.catalogue.attributes._
import commons.catalogue.memory.builder.MemoryBuilder


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
trait Clothing extends CatalogueItem {

  __appendItemTypeGroup(ItemTypeGroup.Clothing)

  def brand: Brand = null
  def price: Price = null
  def sizes: ClothingSizes = null
  def colors: Colors = null

}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object Clothing {

  // Clothing inherits from Catalogue Item
  val SEGMENT_IDX = CatalogueItem.SEGMENT_IDX + 1


  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////

  val NUM_ATTRIBUTES = 4

  val BRAND_ATTR_IDX  = 0
  val SIZES_ATTR_IDX  = 1
  val COLORS_ATTR_IDX = 2
  val PRICE_ATTR_IDX  = 3

  val BRAND_PRIMARY_HEAD_OFFSET  = 0
  val SIZES_PRIMARY_HEAD_OFFSET  = BRAND_PRIMARY_HEAD_OFFSET + Brand.HEAD_SIZE_BYTES
  val COLORS_PRIMARY_HEAD_OFFSET = SIZES_PRIMARY_HEAD_OFFSET + ClothingSizes.HEAD_SIZE_BYTES
  val PRICE_PRIMARY_HEAD_OFFSET  = COLORS_PRIMARY_HEAD_OFFSET + Colors.HEAD_SIZE_BYTES

  val PRIMARY_HEAD_SIZE_BYTES = {
    val totalSize = PRICE_PRIMARY_HEAD_OFFSET + Price.HEAD_SIZE_BYTES
    (totalSize + 7) & ~7
  }


  ///////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// CLOTHING BUILDERS ///////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] trait ClothingBuilder[B <: ClothingBuilder[B]]
    extends CatalogueItem.CatalogueItemBuilder[B] { self: B =>

    private var _brand: Brand = _
    private var _price: Price = _
    private var _sizes: ClothingSizes = _
    private var _colors: Colors = _

    def clothing(brand: Brand, price: Price, sizes: ClothingSizes, colors: Colors): B = {
      _brand = brand
      _price = price
      _sizes = sizes
      _colors = colors
      this
    }

    def writeTo(builder: MemoryBuilder, brand: Brand, price: Price, sizes: ClothingSizes, colors: Colors) {
      builder.begin(NUM_ATTRIBUTES, PRIMARY_HEAD_SIZE_BYTES)
      builder.writeAttr(brand, BRAND_ATTR_IDX, BRAND_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(price, PRICE_ATTR_IDX, PRICE_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(sizes, SIZES_ATTR_IDX, SIZES_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(colors, COLORS_ATTR_IDX, COLORS_PRIMARY_HEAD_OFFSET)
      builder.end()
    }

    override def setAttributes() = {
      super.setAttributes()
      writeTo(builder, _brand, _price, _sizes, _colors)
    }

  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] abstract class BrandItemBuilder[B <: BrandItemBuilder[B]]
    extends CatalogueItem.BrandItemBuilder[B] with ClothingBuilder[B] { self: B => }

  private[catalogue] abstract class StoreItemBuilder[B <: StoreItemBuilder[B]]
    extends CatalogueItem.BrandItemBuilder[B] with ClothingBuilder[B] { self: B => }

}