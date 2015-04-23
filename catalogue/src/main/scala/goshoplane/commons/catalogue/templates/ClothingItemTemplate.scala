package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue._

import com.goshoplane.common._

class ClothingItemTemplate(
  implicit colorsTemplate: ColorsTemplate,
           sizesTemplate : SizesTemplate,
           itemTypeGroupsTemplate: ItemTypeGroupsTemplate,
           productImageTemplate: ProductImageTemplate) extends AbstractTemplate[ClothingItem] {


  def write(packer: Packer, from: ClothingItem, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(15)
      packer.write(from.itemId.storeId.stuid)
      packer.write(from.itemId.cuid)
      packer.write(from.itemType.value)
      itemTypeGroupsTemplate.write(packer, from.itemTypeGroups, required)
      packer.write(from.namedType.name)
      packer.write(from.productTitle.title)
      productImageTemplate.write(packer, from.productImage, required)
      colorsTemplate.write(packer, from.colors, required)
      sizesTemplate.write(packer, from.sizes,  required)
      packer.write(from.brand.name)
      packer.write(from.description.text)
      packer.write(from.price.value)
      packer.write(from.fabric.fabric)
      packer.write(from.fit.fit)
      packer.write(from.style.style)
      packer.writeArrayEnd
    }
  }



  def read(unpacker: Unpacker, q: ClothingItem, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[ClothingItem]
    } else {
      unpacker.readArrayBegin
      val stuid          = unpacker.read(Templates.TLong)
      val cuid           = unpacker.read(Templates.TLong)
      val itemType       = unpacker.read(Templates.TInteger)
      val itemTypeGroups = itemTypeGroupsTemplate.read(unpacker, null.asInstanceOf[ItemTypeGroups], required)
      val namedType      = unpacker.read(Templates.TString)
      val title          = unpacker.read(Templates.TString)
      val image          = productImageTemplate.read(unpacker, null.asInstanceOf[ProductImage], required)
      val colors         = colorsTemplate.read(unpacker, null.asInstanceOf[Colors], required)
      val sizes          = sizesTemplate.read(unpacker, null.asInstanceOf[Sizes], required)
      val brand          = unpacker.read(Templates.TString)
      val description    = unpacker.read(Templates.TString)
      val price          = unpacker.read(Templates.TFloat)
      val fabric         = unpacker.read(Templates.TString)
      val fit            = unpacker.read(Templates.TString)
      val style          = unpacker.read(Templates.TString)
      unpacker.readArrayEnd

      ClothingItem(
        itemId         = CatalogueItemId(storeId = StoreId(stuid = stuid), cuid = cuid),
        itemType       = ItemType.get(itemType).getOrElse(ItemType.Unknown),
        itemTypeGroups = itemTypeGroups,
        namedType      = NamedType(namedType),
        productTitle   = ProductTitle(title),
        colors         = colors,
        sizes          = sizes,
        brand          = Brand(brand),
        description    = Description(description),
        price          = Price(price),
        fabric         = ApparelFabric(fabric),
        fit            = ApparelFit(fit),
        style          = ApparelStyle(style),
        productImage   = image)
    }
  }
}