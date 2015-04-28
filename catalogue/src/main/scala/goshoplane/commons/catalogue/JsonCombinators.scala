package goshoplane.commons.catalogue

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.common._

object JsonCombinators {

  implicit val storeIdWrites: Writes[StoreId] =
    (__ \ "stuid").write[Long].contramap[StoreId](_.stuid)


  implicit val catalogueItemIdWrites: Writes[CatalogueItemId] = (
    (__ \ "storeId").write[StoreId] ~
    (__ \ "ctuid")  .write[Long]
  ) { cid: CatalogueItemId => (cid.storeId, cid.cuid)}

  implicit val itemTypeWrites: Writes[ItemType] =
    Writes(it => JsString(it.name))

  implicit val itemTypeGroupWrites: Writes[ItemTypeGroup.Value] =
    Writes(itg => JsString(itg.toString))

  implicit val itemTypeGroupsWrites: Writes[ItemTypeGroups] =
    Writes(itgs => JsArray(itgs.groups.map(Json.toJson(_)).toSeq))

  implicit val namedTypeWrites: Writes[NamedType] =
    Writes(nt => JsString(nt.name))

  implicit val productTitleWrites: Writes[ProductTitle] =
    Writes(pt => JsString(pt.title))

  implicit val productImageWrites: Writes[ProductImage] = (
    (__ \ "small") .write[String] ~
    (__ \ "medium").write[String] ~
    (__ \ "large") .write[String]
  ) { pi: ProductImage => (pi.small, pi.medium, pi.large) }

  implicit val colorsWrites: Writes[Colors] =
    Writes(c => JsArray(c.values.map(Json.toJson(_)).toSeq))

  implicit val sizesWrites: Writes[Sizes] =
    Writes(s => JsArray(s.values.map(Json.toJson(_)).toSeq))

  implicit val brandWrites: Writes[Brand] =
    Writes(b => JsString(b.name))

  implicit val descriptionWrites: Writes[Description] =
    Writes(d => JsString(d.text))

  implicit val priceWrites: Writes[Price] =
    Writes(p => JsNumber(p.value))

  implicit val apparelFabricWrites: Writes[ApparelFabric] =
    Writes(f => JsString(f.fabric))

  implicit val apparelFitWrites: Writes[ApparelFit] =
    Writes(f => JsString(f.fit))

  implicit val apparelStyleWrites: Writes[ApparelStyle] =
    Writes(s => JsString(s.style))

  implicit val clothingItemWrites: Writes[ClothingItem] = (
    (__ \ "itemId")         .write[CatalogueItemId] ~
    (__ \ "itemType")       .write[ItemType] ~
    (__ \ "itemTypeGroups") .write[ItemTypeGroups] ~
    (__ \ "namedType")      .write[NamedType] ~
    (__ \ "productTitle")   .write[ProductTitle] ~
    (__ \ "productImage")   .write[ProductImage] ~
    (__ \ "colors")         .write[Colors] ~
    (__ \ "sizes")          .write[Sizes] ~
    (__ \ "brand")          .write[Brand] ~
    (__ \ "description")    .write[Description] ~
    (__ \ "price")          .write[Price] ~
    (__ \ "fabric")         .write[ApparelFabric] ~
    (__ \ "fit")            .write[ApparelFit] ~
    (__ \ "style")          .write[ApparelStyle]
  )(unlift(ClothingItem.unapply))


}