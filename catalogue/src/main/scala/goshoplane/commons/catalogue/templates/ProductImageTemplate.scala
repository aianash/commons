package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue._

import com.goshoplane.common._

class ProductImageTemplate extends AbstractTemplate[ProductImage] {

  def write(packer: Packer, from: ProductImage, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(3)
      packer.write(from.small)
      packer.write(from.medium)
      packer.write(from.large)
      packer.writeArrayEnd
    }
  }


  def read(unpacker: Unpacker, q: ProductImage, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[ProductImage]
    } else {
      unpacker.readArrayBegin
      val small   = unpacker.read(Templates.TString)
      val medium  = unpacker.read(Templates.TString)
      val large   = unpacker.read(Templates.TString)
      unpacker.readArrayEnd

      ProductImage(small, medium, large)
    }
  }
}