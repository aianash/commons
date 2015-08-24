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

  def itemTypeGroup = ItemType.Clothing

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
  def sizes: ClothingSizes = {
    ???
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def colors: Colors = {
    ???
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def asClothing =
    new Clothing(afterItemTypeGroupIsSetTo(memory.truncateTo(SEGMENT_IDX), ItemType.Clothing))

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

  // CatalogueItem.ownerTypeOf(binary) match {
  //   case BRAND => new Clothing(PrimaryMemory(binary))
  //   case _ => throw new IllegalArgumentException("OwnerType of parameters not BRAND")
  // }

  private[catalogue] def apply(binary: Array[Byte], brandItem: CatalogueItem) =
    ifStore(binary, thenWithBrand = brandItem) { memory =>
      new Clothing(memory)
    }

  // CatalogueItem.ownerTypeOf(binary) match {
  //   case STORE if brandItem.memory.isPrimary && brandItem.isInstanceOf[Clothing] =>
  //     new Clothing(SecondaryMemory(binary, brandItem.memory))
  //   case _ =>
  //     throw new IllegalArgumentException("OwnerType of parameters is not consistent ie binary with STORE and item with BRAND type")
  // }


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

}