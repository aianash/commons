package goshoplane.commons.catalogue.kafka.serializers

import kafka.serializer.Encoder

import org.msgpack.ScalaMessagePack

import com.goshoplane.common._

import goshoplane.commons.catalogue.templates.SerializedCatalogueItemTemplate

class SerializedCatalogueItemEncoder extends Encoder[SerializedCatalogueItem] {
  implicit val template = new SerializedCatalogueItemTemplate

  override def toBytes(item: SerializedCatalogueItem) =
    ScalaMessagePack.writeT(item)
}