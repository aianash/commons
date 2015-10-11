package commons.catalogue

import scala.collection.JavaConversions._

import java.util.ArrayList

import commons.core.util.UnsafeUtil
import commons.catalogue.attributes._
import commons.catalogue.memory.builder.{MemoryBuilder, PrimaryMemoryBuilder, SecondaryMemoryBuilder}
import commons.catalogue.memory.{Memory, PrimaryMemory}

import commons.owner._

import play.api.libs.json._


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
abstract class CatalogueItem(private[catalogue] val memory: Memory) extends Equals {

  import UnsafeUtil._
  import CatalogueItem._

  val itemTypeGroup: ItemTypeGroup = ItemTypeGroup(memory.getIntAt(ITEM_TYPE_GROUP_CORE_OFFSET_BYTES))

  // set by inheriting class just for sanity checks
  protected def instanceItemTypeGroup: ItemTypeGroup

  require(instanceItemTypeGroup >> itemTypeGroup,
    new IllegalArgumentException("ItemTypeGroup from array doesnot match itemType of the instantiating catalogue item subclass"))

  private val numSegments = memory.getShortAt(CORE_ATTRIBUTES_SIZE_BYTES)

  /** IDs */
  val ownerId = {
    val ownerType = memory.getCharAt(OWNER_ID_CORE_OFFSET_BYTES)
    val owuid = memory.getLongAt(OWNER_ID_CORE_OFFSET_BYTES + OwnerType.SIZE_BYTES)
    OwnerId(OwnerType(ownerType), owuid)
  }

  def ownerType = ownerId.ownerType

  val itemId = CatalogueItemId(memory.getLongAt(CATALOGUE_ITEM_ID_CORE_OFFSET_BYTES))

  val variantId = VariantId(memory.getLongAt(VARIANT_ID_CORE_OFFSET_BYTES))

  /** Attributes */

  def namedType: NamedType = {
    val prepared = memory.prepareFor(classOf[NamedType], SEGMENT_IDX, NAMED_TYPE_ATTR_IDX, NAMED_TYPE_PRIMARY_HEAD_OFFSET)
    NamedType.read(prepared)
  }

  def productTitle: ProductTitle = {
    val prepared = memory.prepareFor(classOf[ProductTitle], SEGMENT_IDX, PRODUCT_TITLE_ATTR_IDX, PRODUCT_TITLE_PRIMARY_HEAD_OFFSET)
    ProductTitle.read(prepared)
  }

  protected def afterItemTypeGroupIsSetTo(memory: Memory, itemTypeGroup: ItemTypeGroup) = {
    UNSAFE.putInt(memory.underlying, BYTE_ARRAY_BASE_OFFSET + ITEM_TYPE_GROUP_CORE_OFFSET_BYTES, itemTypeGroup.id)
    memory
  }

  override def equals(that: Any) = that match {
    case that: CatalogueItem =>
      (this eq that) ||
      ((this canEqual that) &&
      (this.ownerId equals that.ownerId) &&
      (this.itemId equals that.itemId))
    case _ => false
  }

  override def hashCode() = {
    val arr = Array.ofDim[Byte](OwnerId.SIZE_BYTES + CatalogueItemId.SIZE_BYTES)
    UNSAFE.copyMemory(memory.underlying, BYTE_ARRAY_BASE_OFFSET, arr, BYTE_ARRAY_BASE_OFFSET, OwnerId.SIZE_BYTES + CatalogueItemId.SIZE_BYTES)
    scala.util.hashing.MurmurHash3.bytesHash(arr)
  }

  def json = Json.obj(
    "cuid"          -> itemId.cuid,
    "vrtuid"        -> variantId.vrtuid,
    "owner"         -> Json.obj(
      "owuid"         -> ownerId.owuid,
      "type"          -> ownerType.name
    ),
    "namedType"     -> namedType.name,
    "title"         -> productTitle.title,
    "itemTypeGroup" -> itemTypeGroup.toString,
    "groups"        -> itemTypeGroup.groups.map(_.toString)
  )

}


/** Description of function
  *
  * @param Parameter1 - blah blah
  * @return Return value - blah blah
  */
object CatalogueItem extends CatalogueItemUtilMethods {

  // segment idx for CatalogueItem, top of the hierarchy
  val SEGMENT_IDX = 0

  /** Core Attributes offset in the header of memory
    * starting from zero index.
    *
    * It is important to keep only fixed size attributes
    * in the header section. This fascilitates in easily finding the start of
    * segment offsets from segment table in memory header
    */

  private[catalogue] val OWNER_ID_CORE_OFFSET_BYTES          = 0
  private[catalogue] val CATALOGUE_ITEM_ID_CORE_OFFSET_BYTES = OWNER_ID_CORE_OFFSET_BYTES + OwnerId.SIZE_BYTES
  private[catalogue] val VARIANT_ID_CORE_OFFSET_BYTES        = CATALOGUE_ITEM_ID_CORE_OFFSET_BYTES + CatalogueItemId.SIZE_BYTES
  private[catalogue] val ITEM_TYPE_GROUP_CORE_OFFSET_BYTES   = VARIANT_ID_CORE_OFFSET_BYTES + VariantId.SIZE_BYTES

  private[catalogue] val CORE_ATTRIBUTES_SIZE_BYTES = ITEM_TYPE_GROUP_CORE_OFFSET_BYTES + ItemTypeGroup.SIZE_BYTES


  ///////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// ATTRIBUTE CONSTANTS /////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////

  val NUM_ATTRIBUTES = 2

  /** Attribute indexes in the catalogue item segment */

  val NAMED_TYPE_ATTR_IDX    = 0
  val PRODUCT_TITLE_ATTR_IDX = 1

  val NAMED_TYPE_PRIMARY_HEAD_OFFSET    = 0
  val PRODUCT_TITLE_PRIMARY_HEAD_OFFSET = NAMED_TYPE_PRIMARY_HEAD_OFFSET + NamedType.HEAD_SIZE_BYTES

  val PRIMARY_HEAD_SIZE_BYTES = {
    val totalSize = PRODUCT_TITLE_PRIMARY_HEAD_OFFSET + ProductTitle.HEAD_SIZE_BYTES
    (totalSize + 7) & ~7 // word align
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// CATALOGUE ITEM BUILDERS ///////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  private[catalogue] class BuilderFatory[B <: Builder[B, BrandId], S <: Builder[S, StoreId]](
    brandItemBuilderFactory: => B,
    storeItemBuilderFactory: CatalogueItem => S
  ) {

    def forBrand = brandItemBuilderFactory

    def forStore = new {
      def using(item: CatalogueItem) = storeItemBuilderFactory(item)
    }
  }

  /** This trait defines reusable functions used by
    * item builders
    */
  private[catalogue] trait Builder[B <: Builder[B, OW], OW <: OwnerId] { self: B =>

    type I <: CatalogueItem

    protected def builder: MemoryBuilder

    def numSegments: Int
    def itemTypeGroup: ItemTypeGroup

    private var _ownerId: OW = _
    private var _itemId: CatalogueItemId = CatalogueItemId.NULL
    private var _variantId: VariantId = VariantId.NULL
    private var _title: ProductTitle = null
    private var _namedType: NamedType = null

    def title(title: ProductTitle): B = {
      _title = title
      this
    }

    def title(title: String): B = {
      _title = ProductTitle(title)
      this
    }

    def namedType(namedType: NamedType): B = {
      _namedType = namedType
      this
    }

    def namedType(namedType: String): B = {
      _namedType = NamedType(namedType)
      this
    }

    def ids(ownerId: OW, itemId: CatalogueItemId, variantId: VariantId): B = {
      _ownerId = ownerId
      _itemId = itemId
      _variantId = variantId
      this
    }

    def setAttributes() {
      writeCoreAttributesTo(builder, _ownerId, _itemId, _variantId, itemTypeGroup)
      writeTo(builder, _namedType, _title)
    }

    def build: I

    /** Description of function
      *
      * @param Parameter1 - blah blah
      * @return Return value - blah blah
      */
    private def writeCoreAttributesTo(builder: MemoryBuilder, ownerId: OW, itemId: CatalogueItemId, variantId: VariantId, itemTypeGroup: ItemTypeGroup) {
      builder.beginHeader()
      builder.putCharAt(OWNER_ID_CORE_OFFSET_BYTES, ownerId.ownerType.code)
      builder.putLongAt(OWNER_ID_CORE_OFFSET_BYTES + OwnerType.SIZE_BYTES, ownerId.owuid)
      builder.putLongAt(CATALOGUE_ITEM_ID_CORE_OFFSET_BYTES, itemId.cuid)
      builder.putLongAt(VARIANT_ID_CORE_OFFSET_BYTES, variantId.vrtuid)
      builder.putIntAt(ITEM_TYPE_GROUP_CORE_OFFSET_BYTES, itemTypeGroup.id)
      builder.endHeader()
    }

    /** Description of function
      *
      * @param Parameter1 - blah blah
      * @return Return value - blah blah
      */
    private def writeTo(builder: MemoryBuilder, namedType: NamedType, productTitle: ProductTitle) {
      builder.begin(CatalogueItem.NUM_ATTRIBUTES, CatalogueItem.PRIMARY_HEAD_SIZE_BYTES)
      builder.writeAttr(namedType, NAMED_TYPE_ATTR_IDX, NAMED_TYPE_PRIMARY_HEAD_OFFSET)
      builder.writeAttr(productTitle, PRODUCT_TITLE_ATTR_IDX, PRODUCT_TITLE_PRIMARY_HEAD_OFFSET)
      builder.end()
    }

  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] trait BrandItemBuilder[B <: Builder[B, BrandId]] { self: B =>
    protected val builder = new PrimaryMemoryBuilder(numSegments)
  }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  private[catalogue] trait StoreItemBuilder[B <: Builder[B, StoreId]] { self: B =>

    def brandItem: CatalogueItem

    require(brandItem.itemTypeGroup equals itemTypeGroup,
      new IllegalArgumentException("Brand item is not of same itemTypeGroup as " + itemTypeGroup.toString))

    require(brandItem.ownerId.ownerType equals BRAND,
      new IllegalArgumentException("Catalogue items's ownerType is not BRAND"))

    protected val builder = {
      brandItem.memory match {
        case primary: PrimaryMemory =>
          new SecondaryMemoryBuilder(brandItem.memory.asInstanceOf[PrimaryMemory], numSegments)
        case _ =>
          throw new IllegalArgumentException("Currently only support creating store item from non-inherited brand item")
      }
    }

  }

}