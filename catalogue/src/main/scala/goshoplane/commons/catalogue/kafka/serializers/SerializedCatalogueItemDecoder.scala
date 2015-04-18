package goshoplane.commons.catalogue.kafka.serializers

import kafka.serializer.Decoder

import org.msgpack.ScalaMessagePack

import com.goshoplane.common._

import goshoplane.commons.catalogue.templates.SerializedCatalogueItemTemplate


class SerializedCatalogueItemDecoder extends Decoder[SerializedCatalogueItem] {
  private val template = new SerializedCatalogueItemTemplate

  override def fromBytes(bytes: Array[Byte]) = {
    ScalaMessagePack.messagePack.read(bytes, template)
  }

}