package goshoplane.commons.catalogue.templates

import java.nio.ByteBuffer

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import com.goshoplane.common._

class SerializedCatalogueItemTemplate extends AbstractTemplate[SerializedCatalogueItem] {

  def write(packer: Packer, from: SerializedCatalogueItem, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      val byteBuffer = from.stream
      val bytes = Array.ofDim[Byte](byteBuffer.remaining)
      byteBuffer.get(bytes)

      packer.writeArrayBegin(bytes.size + 4)
      packer.write(from.itemId.storeId.stuid)
      packer.write(from.itemId.cuid)
      packer.write(from.serializerId.sid)
      packer.write(from.serializerId.stype.value)

      for(byte <- bytes) packer.write(byte)

      packer.writeArrayEnd
    }
  }


  def read(unpacker: Unpacker, q: SerializedCatalogueItem, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[SerializedCatalogueItem]
    } else {
      val bytesArraySize = unpacker.readArrayBegin - 4

      val stuid = unpacker.read(Templates.TLong)
      val cuid  = unpacker.read(Templates.TLong)
      val sid   = unpacker.read(Templates.TString)
      val stype = SerializerType.get(unpacker.read(Templates.TInteger)).getOrElse(SerializerType.Unknown)

      val bytes = Array.ofDim[Byte](bytesArraySize)

      for(i <- 0 until bytesArraySize) {
        bytes(i) = unpacker.read(Templates.TByte)
      }

      val stream = ByteBuffer.wrap(bytes)

      unpacker.readArrayEnd

      SerializedCatalogueItem(
        itemId = CatalogueItemId(storeId = StoreId(stuid), cuid = cuid),
        serializerId = SerializerId(sid = sid, stype = stype),
        stream = stream)
    }
  }
}