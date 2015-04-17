package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue._

import com.goshoplane.common._

class ClothingItemTemplate(
  implicit colorsTemplate: ColorsTemplate,
           sizesTemplate : SizesTemplate) extends AbstractTemplate[ClothingItem] {


  def write(packer: Packer, from: ClothingItem, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(7)
      packer.write(from.itemId.storeId.stuid)
      packer.write(from.itemId.cuid)
      colorsTemplate.write(packer, from.colors, required)
      sizesTemplate.write(packer, from.sizes,  required)
      packer.write(from.brand.name)
      packer.write(from.clothingType.value)
      packer.write(from.description.text)
      packer.writeArrayEnd
    }
  }



  def read(unpacker: Unpacker, q: ClothingItem, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[ClothingItem]
    } else {
      unpacker.readArrayBegin
      val stuid        = unpacker.read(Templates.TLong)
      val cuid         = unpacker.read(Templates.TLong)
      val colors       = colorsTemplate.read(unpacker, null.asInstanceOf[Colors], required)
      val sizes        = sizesTemplate.read(unpacker, null.asInstanceOf[Sizes], required)
      val brand        = unpacker.read(Templates.TString)
      val clothingType = unpacker.read(Templates.TString)
      val description  = unpacker.read(Templates.TString)
      unpacker.readArrayEnd

      val storeId = StoreId(stuid = stuid)

      ClothingItem(
        itemId       = CatalogueItemId(storeId = storeId, cuid = cuid),
        colors       = colors,
        sizes        = sizes,
        brand        = Brand(brand),
        clothingType = ClothingType(clothingType),
        description  = Description(description))
    }
  }
}