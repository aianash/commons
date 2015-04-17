package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue.Colors

class ColorsTemplate extends AbstractTemplate[Colors] {

  def write(packer: Packer, from: Colors, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(from.values.size)
      for(color <- from.values) packer.write(color)
      packer.writeArrayEnd
    }
  }



  def read(unpacker: Unpacker, q: Colors, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Colors]
    } else {
      val size = unpacker.readArrayBegin
      val values = Array.ofDim[String](size)
      for(i <- 0 until size) {
        values(i) = unpacker.read(Templates.TString)
      }
      unpacker.readArrayEnd

      Colors(values = values)
    }
  }

}