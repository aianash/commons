package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue._

import com.goshoplane.common._

class ClothingItemTemplate(
  implicit colorsTemplate: ColorsTemplate,
           sizesTemplate : SizesTemplate,
           itemTypeGroupsTemplate: ItemTypeGroupsTemplate) extends AbstractTemplate[ClothingItem] {


  def write(packer: Packer, from: ClothingItem, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(11)
      packer.write(from.itemId.storeId.stuid)
      packer.write(from.itemId.cuid)
      packer.write(from.itemType.value)
      itemTypeGroupsTemplate.write(packer, from.itemTypeGroups, required)
      packer.write(from.namedType.name)
      packer.write(from.productTitle.title)
      colorsTemplate.write(packer, from.colors, required)
      sizesTemplate.write(packer, from.sizes,  required)
      packer.write(from.brand.name)
      packer.write(from.description.text)
      packer.write(from.price.value)
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
      val name           = unpacker.read(Templates.TString)
      val title          = unpacker.read(Templates.TString)
      val colors         = colorsTemplate.read(unpacker, null.asInstanceOf[Colors], required)
      val sizes          = sizesTemplate.read(unpacker, null.asInstanceOf[Sizes], required)
      val brand          = unpacker.read(Templates.TString)
      val description    = unpacker.read(Templates.TString)
      val price          = unpacker.read(Templates.TFloat)
      unpacker.readArrayEnd

      val storeId        = StoreId(stuid = stuid)
      val namedType      = NamedType(name)
      val productTitle   = ProductTitle(title)

      ClothingItem(
        itemId         = CatalogueItemId(storeId = storeId, cuid = cuid),
        itemType       = ItemType.get(itemType).getOrElse(ItemType.Unknown),
        itemTypeGroups = itemTypeGroups,
        namedType      = namedType,
        productTitle   = productTitle,
        colors         = colors,
        sizes          = sizes,
        brand          = Brand(brand),
        description    = Description(description),
        price          = Price(price))
    }
  }
}