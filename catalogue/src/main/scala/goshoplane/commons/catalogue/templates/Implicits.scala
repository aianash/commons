package goshoplane.commons.catalogue.templates

object Implicits {
  implicit val sizesTemplate          = new SizesTemplate
  implicit val colorsTemplate         = new ColorsTemplate
  implicit val itemTypeGroupsTemplate = new ItemTypeGroupsTemplate
  implicit val productImageTemplate   = new ProductImageTemplate
  implicit val clothingItemTemplate   = new ClothingItemTemplate
}