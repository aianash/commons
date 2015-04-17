package goshoplane.commons.catalogue.kafka.serializers

import org.apache.kafka.common.serialization.Serializer

import org.msgpack.ScalaMessagePack

import com.goshoplane.common._

import goshoplane.commons.catalogue.templates.SerializedCatalogueItemTemplate

class SerializedCatalogueItemSerializer extends Serializer[SerializedCatalogueItem] {
  implicit val template = new SerializedCatalogueItemTemplate

  override def configure(configs: java.util.Map[String, _], isKey: Boolean) {}

  override def serialize(topic: String, data: SerializedCatalogueItem) = {
    ScalaMessagePack.writeT(data)
  }

  override def close() {}
}
