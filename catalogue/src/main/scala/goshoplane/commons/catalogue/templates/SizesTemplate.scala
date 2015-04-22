package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue.Sizes

class SizesTemplate extends AbstractTemplate[Sizes] {

  def write(packer: Packer, from: Sizes, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(from.values.size)
      for(color <- from.values) packer.write(color)
      packer.writeArrayEnd
    }
  }



  def read(unpacker: Unpacker, q: Sizes, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Sizes]
    } else {
      val size = unpacker.readArrayBegin
      val values = Array.ofDim[String](size)
      for(i <- 0 until size) {
        values(i) = unpacker.read(Templates.TString)
      }
      unpacker.readArrayEnd

      Sizes(values = values)
    }
  }

}