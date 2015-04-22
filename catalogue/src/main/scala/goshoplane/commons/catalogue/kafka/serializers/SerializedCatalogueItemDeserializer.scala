package goshoplane.commons.catalogue.kafka.serializers

import org.apache.kafka.common.serialization.Deserializer

import org.msgpack.ScalaMessagePack

import com.goshoplane.common._

import goshoplane.commons.catalogue.templates.SerializedCatalogueItemTemplate

class SerializedCatalogueItemDeserializer extends Deserializer[SerializedCatalogueItem] {
  implicit val template = new SerializedCatalogueItemTemplate

  override def configure(configs: java.util.Map[String, _], isKey: Boolean) {}

  override def deserialize(topic: String, bytes: Array[Byte]) = {
    ScalaMessagePack.messagePack.read(bytes, template)
  }

  override def close() {}
}
