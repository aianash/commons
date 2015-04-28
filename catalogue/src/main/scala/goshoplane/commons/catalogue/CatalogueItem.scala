package goshoplane.commons.catalogue

import java.nio.ByteBuffer

import scala.collection.mutable.{Map => MutableMap}

import com.goshoplane.common._

import org.msgpack.ScalaMessagePack
import org.msgpack.annotation.Message

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._

import play.api.libs.json._

import JsonCombinators._

sealed trait CatalogueItem extends Serializable {
  def itemId: CatalogueItemId
  def itemType: ItemType
  def itemTypeGroups: ItemTypeGroups
  def namedType: NamedType
  def productTitle: ProductTitle
  def productImage: ProductImage

  def attributesMap: Map[String, String]
  def json: JsValue
}


object CatalogueItem {

  import templates.Implicits._


  def decode(serializedItem: SerializedCatalogueItem): Option[CatalogueItem] =
    serializedItem.serializerId match {

      case SerializerId("clothing-item-1", SerializerType.Msgpck) =>
        val stream = serializedItem.stream
        val bytes  = Array.ofDim[Byte](stream.remaining)
        stream.mark
        stream.get(bytes)
        stream.reset

        ScalaMessagePack.messagePack.read(bytes, clothingItemTemplate).some


      case _ => None
    }


  def decode(jsonItem: JsonCatalogueItem): Option[CatalogueItem] =
    jsonItem.versionId match {

      case "clothing-item-1" =>
        Json.parse(jsonItem.json).validate[ClothingItem] match {
          case JsSuccess(clothingItem, _) => clothingItem.some
          case JsError(_) => None
        }

      case _ => None
    }


  def encode(catalogueItem: CatalogueItem): Option[SerializedCatalogueItem] =
    catalogueItem match {

      case ci: ClothingItem =>
        val bytes        = ScalaMessagePack.writeT(ci)
        val stream       = ByteBuffer.wrap(bytes)
        val serializerId = SerializerId("clothing-item-1", SerializerType.Msgpck)

        SerializedCatalogueItem(
          itemId       = ci.itemId,
          serializerId = serializerId,
          stream       = stream).some

      case _ => None
    }


  def asJsonItem(catalogueItem: CatalogueItem): Option[JsonCatalogueItem] =
    scala.util.Try({
      catalogueItem match {
        case ci: ClothingItem =>
          JsonCatalogueItem(
            itemId    = ci.itemId,
            versionId = "clothing-item-1",
            json      = ci.json.toString).some

        case _ => None
      }
    }).toOption.flatMap(identity)


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
  productImage: ProductImage,
  // attributes
  colors: Colors,
  sizes: Sizes,
  brand: Brand,
  description: Description,
  price: Price,
  fabric: ApparelFabric,
  fit: ApparelFit,
  style: ApparelStyle) extends CatalogueItem {

  lazy val attributesMap = Map(
    "colors"      -> colors.values.mkString(","),
    "sizes"       -> sizes.values.mkString(","),
    "brand"       -> brand.name,
    "description" -> description.text,
    "price"       -> price.value.toString,
    "fabric"      -> fabric.fabric,
    "fit"         -> fit.fit,
    "style"       -> style.style
  )


  lazy val json = Json.toJson(this)
}

object ClothingItem {

  def create(itemId: CatalogueItemId,
             itemType: ItemType,
             itemTypeGroups: ItemTypeGroups,
             namedType: NamedType,
             productTitle: ProductTitle,
             productImage: ProductImage,
             attributes: Map[String, String]) = {

    val colors      = attributes.get("colors").map(_.split(",").toSeq).getOrElse(Seq.empty[String])
    val sizes       = attributes.get("sizes") .map(_.split(",").toSeq).getOrElse(Seq.empty[String])
    val brand       = attributes.get("brand").getOrElse("unknown")
    val description = attributes.get("description").getOrElse("")
    val price       = attributes.get("price").map(_.toFloat).getOrElse(-1.0f)
    val fabric      = attributes.get("fabric").getOrElse("unknown")
    val fit         = attributes.get("fit").getOrElse("unknown")
    val style       = attributes.get("style").getOrElse("unknown")

    ClothingItem(
      itemId         = itemId,
      itemType       = itemType,
      itemTypeGroups = itemTypeGroups,
      namedType      = namedType,
      productTitle   = productTitle,
      productImage   = productImage,
      colors         = Colors(colors),
      sizes          = Sizes(sizes),
      brand          = Brand(brand),
      description    = Description(description),
      price          = Price(price),
      fabric         = ApparelFabric(fabric),
      fit            = ApparelFit(fit),
      style          = ApparelStyle(style)
    )
  }
}
