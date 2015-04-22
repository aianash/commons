package goshoplane.commons.catalogue

import java.nio.ByteBuffer

import com.goshoplane.common._

import org.msgpack.ScalaMessagePack
import org.msgpack.annotation.Message

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._



sealed trait CatalogueItem extends Serializable {
  def itemId: CatalogueItemId
  def itemType: ItemType
  def itemTypeGroups: ItemTypeGroups
  def namedType: NamedType
  def productTitle: ProductTitle
}


object CatalogueItem {

  import templates.Implicits._


  def decode(serializedItem: SerializedCatalogueItem): Option[CatalogueItem] =
    serializedItem.serializerId match {

      case SerializerId("clothing-item", SerializerType.Msgpck) =>
        val stream = serializedItem.stream
        val bytes  = Array.ofDim[Byte](stream.remaining)
        stream.mark
        stream.get(bytes)
        stream.reset

        ScalaMessagePack.messagePack.read(bytes, clothingItemTemplate).some


      case _ => None
    }



  def encode(catalogueItem: CatalogueItem): Option[SerializedCatalogueItem] =
    catalogueItem match {

      case ci: ClothingItem =>
        val bytes        = ScalaMessagePack.writeT(ci)
        val stream       = ByteBuffer.wrap(bytes)
        val serializerId = SerializerId("clothing-item", SerializerType.Msgpck)

        SerializedCatalogueItem(
          itemId       = ci.itemId,
          serializerId = serializerId,
          stream       = stream).some

      case _ => None
    }

}




/////////////////////////////////////////////////////////////////////////////
////////////////////// Individual Catalogue items ///////////////////////////
/////////////////////////////////////////////////////////////////////////////


@Message
case class ClothingItem(
  itemId: CatalogueItemId,
  itemType: ItemType,
  itemTypeGroups: ItemTypeGroups,
  namedType: NamedType,
  productTitle: ProductTitle,
  colors: Colors,
  sizes: Sizes,
  brand: Brand,
  description: Description,
  price: Price) extends CatalogueItem
