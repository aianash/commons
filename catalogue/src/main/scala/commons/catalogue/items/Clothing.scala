package commons
package catalogue
package items

import commons.catalogue._
import commons.catalogue.attributes._
import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.{Memory, PrimaryMemory, SecondaryMemory}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
class Clothing(memory: Memory) extends CatalogueItem(memory) {

  import Clothing._

  override def instanceItemTypeGroup: ItemTypeGroup = ItemTypeGroup.Clothing

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def brand: Brand = {
    val prepared = memory.prepareFor(classOf[Brand], SEGMENT_IDX, BRAND_ATTR_IDX, BRAND_PRIMARY_HEAD_OFFSET)
    Brand.read(prepared)
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def price: Price = {
    val prepared = memory.prepareFor(classOf[Price], SEGMENT_IDX, PRICE_ATTR_IDX, PRICE_PRIMARY_HEAD_OFFSET)
    Price.read(prepared)
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def sizes: ClothingSizes =
    ClothingSizes.read(memory.prepareFor(classOf[ClothingSizes], SEGMENT_IDX, SIZES_ATTR_IDX, SIZES_PRIMARY_HEAD_OFFSET))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def colors: Colors =
    Colors.read(memory.prepareFor(classOf[Colors], SEGMENT_IDX, COLORS_ATTR_IDX, COLORS_PRIMARY_HEAD_OFFSET))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def styles: ClothingStyles =
    ClothingStyles.read(memory.prepareFor(classOf[ClothingStyles], SEGMENT_IDX, CLOTHING_STYLES_ATTR_IDX, CLOTHING_STYLES_PRIMARY_HEAD_OFFSET))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def descr: Description =
    Description.read(memory.prepareFor(classOf[Description], SEGMENT_IDX, DESCRIPTION_ATTR_IDX, DESCRIPTION_PRIMARY_HEAD_OFFSET))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def gender: Gender =
    Gender.read(memory.prepareFor(classOf[Gender], SEGMENT_IDX, GENDER_ATTR_IDX, GENDER_PRIMARY_HEAD_OFFSET))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def asClothing =
    new Clothing(afterItemTypeGroupIsSetTo(memory.truncateTo(SEGMENT_IDX), ItemTypeGroup.Clothing))

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  override def canEqual(that: Any) = that match {
    case _: Clothing => true
    case _ => false
  }

}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object Clothing {

  // Clothing inherits from Catalogue Item
  val SEGMENT_IDX = CatalogueItem.SEGMENT_IDX + 1

  private[catalogue] def apply(binary: Array[Byte]) =
    ifBrand(binary) { memory =>
      new Clothing(memory)
    }

  private[catalogue] def apply(binary: Array[Byte], brandItem: CatalogueItem) =
    ifStore(binary, thenWithBrand = brandItem) { memory =>
      new Clothing(memory)
    }


  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////

  val NUM_ATTRIBUTES = 6

  val BRAND_ATTR_IDX           = 0
  val SIZES_ATTR_IDX           = 1
  val COLORS_ATTR_IDX          = 2
  val PRICE_ATTR_IDX           = 3
  val CLOTHING_STYLES_ATTR_IDX = 4
  val DESCRIPTION_ATTR_IDX     = 5
  val GENDER_ATTR_IDX          = 6

  val BRAND_PRIMARY_HEAD_OFFSET           = 0
  val SIZES_PRIMARY_HEAD_OFFSET           = BRAND_PRIMARY_HEAD_OFFSET + Brand.HEAD_SIZE_BYTES
  val COLORS_PRIMARY_HEAD_OFFSET          = SIZES_PRIMARY_HEAD_OFFSET + ClothingSizes.HEAD_SIZE_BYTES
  val PRICE_PRIMARY_HEAD_OFFSET           = COLORS_PRIMARY_HEAD_OFFSET + Colors.HEAD_SIZE_BYTES
  val CLOTHING_STYLES_PRIMARY_HEAD_OFFSET = PRICE_PRIMARY_HEAD_OFFSET + Price.HEAD_SIZE_BYTES
  val DESCRIPTION_PRIMARY_HEAD_OFFSET     = CLOTHING_STYLES_PRIMARY_HEAD_OFFSET + ClothingStyles.HEAD_SIZE_BYTES
  val GENDER_PRIMARY_HEAD_OFFSET          = DESCRIPTION_PRIMARY_HEAD_OFFSET + Description.HEAD_SIZE_BYTES

  val PRIMARY_HEAD_SIZE_BYTES = {
    val totalSize = GENDER_PRIMARY_HEAD_OFFSET + Gender.HEAD_SIZE_BYTES
    (totalSize + 7) & ~7
  }


  ///////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// CLOTHING BUILDER ////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] extends CatalogueItem.Builder[B, OW] { self: B =>

    private var _brand: Brand = _
    private var _price: Price = _
    private var _sizes: ClothingSizes = _
    private var _colors: Colors = _
    private var _itemStyles: ClothingStyles = _
    private var _description: Description = _

    def clothing(brand: Brand, price: Price, sizes: ClothingSizes, colors: Colors, itemStyles: ClothingStyles, description: Description): B = {
      _brand = brand
      _price = price
      _sizes = sizes
      _colors = colors
      _itemStyles = itemStyles
      _description = description
      this
    }

    def writeTo(builder: MemoryBuilder, brand: Brand, price: Price, sizes: ClothingSizes, colors: Colors, itemStyles: ClothingStyles, description: Description) {
      builder.begin(NUM_ATTRIBUTES, PRIMARY_HEAD_SIZE_BYTES)
      builder.writeAttr(brand, BRAND_ATTR_IDX, BRAND_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(price, PRICE_ATTR_IDX, PRICE_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(sizes, SIZES_ATTR_IDX, SIZES_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(colors, COLORS_ATTR_IDX, COLORS_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(itemStyles, CLOTHING_STYLES_ATTR_IDX, CLOTHING_STYLES_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(itemStyles, DESCRIPTION_ATTR_IDX, DESCRIPTION_PRIMARY_HEAD_OFFSET)
      builder.end()
    }

    override def setAttributes() = {
      super.setAttributes()
      writeTo(builder, _brand, _price, _sizes, _colors, _itemStyles, _description)
    }

  }

}