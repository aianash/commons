package goshoplane.commons.catalogue

import scala.util.Try

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.goshoplane.common._

object JsonCombinators {

  import Reads._

  val storeIdReads: Reads[StoreId] =
    (__ \ "stuid").read[String].map(id => StoreId(id.toLong))

  val storeIdWrites: Writes[StoreId] =
    (__ \ "stuid").write[String].contramap[StoreId](_.stuid.toString)

  implicit val storeIdFormat: Format[StoreId] =
    Format(storeIdReads, storeIdWrites)



  val catalogueItemIdReads: Reads[CatalogueItemId] = (
    (__ \ "storeId").read[StoreId] ~
    (__ \ "cuid")   .read[String].map(_.toLong)
  )(CatalogueItemId.apply _)

  val catalogueItemIdWrites: Writes[CatalogueItemId] = (
    (__ \ "storeId").write[StoreId] ~
    (__ \ "cuid")   .write[String]
  ) { cid: CatalogueItemId => (cid.storeId, cid.cuid.toString) }

  implicit val catalogueItemIdFormat: Format[CatalogueItemId] =
    Format(catalogueItemIdReads, catalogueItemIdWrites)



  val itemTypeReads: Reads[ItemType] =
    Reads[ItemType] { json =>
      StringReads.reads(json).flatMap { value =>
        ItemType.valueOf(value) match {
          case Some(itemType) => JsSuccess(itemType)
          case None           => JsError("error.expected.itemType")
        }
      }
    }

  val itemTypeWrites: Writes[ItemType] =
    Writes(it => JsString(it.name))

  implicit val itemTypeFormat: Format[ItemType] =
    Format(itemTypeReads, itemTypeWrites)



  val itemTypeGroupReads: Reads[ItemTypeGroup.Value] =
    Reads[ItemTypeGroup.Value] { json =>
      StringReads.reads(json).flatMap { value =>
        Try {
          ItemTypeGroup.withName(value)
        } map(JsSuccess(_)) getOrElse(JsError("error.expected.itemTypeGroup"))
      }
    }

  val itemTypeGroupWrites: Writes[ItemTypeGroup.Value] =
    Writes(itg => JsString(itg.toString))

  implicit val itemTypeGroupFormat: Format[ItemTypeGroup.Value] =
    Format(itemTypeGroupReads, itemTypeGroupWrites)



  val itemTypeGroupsReads: Reads[ItemTypeGroups] =
    Reads[ItemTypeGroups] { json =>
      traversableReads[Seq, ItemTypeGroup.Value].reads(json).map(ItemTypeGroups(_))
    }

  val itemTypeGroupsWrites: Writes[ItemTypeGroups] =
    Writes(itgs => JsArray(itgs.groups.map(Json.toJson(_)).toSeq))

  implicit val itemTypeGroupsFormat: Format[ItemTypeGroups] =
    Format(itemTypeGroupsReads, itemTypeGroupsWrites)



  val namedTypeReads: Reads[NamedType] =
    Reads[NamedType] { json =>
      StringReads.reads(json).map(NamedType(_))
    }

  val namedTypeWrites: Writes[NamedType] =
    Writes(nt => JsString(nt.name))

  implicit val namedTypeFomat: Format[NamedType] =
    Format(namedTypeReads, namedTypeWrites)



  val productTitleReads: Reads[ProductTitle] =
    Reads[ProductTitle] { json =>
      StringReads.reads(json).map(ProductTitle(_))
    }

  val productTitleWrites: Writes[ProductTitle] =
    Writes(pt => JsString(pt.title))

  implicit val productTitleFormat: Format[ProductTitle] =
    Format(productTitleReads, productTitleWrites)



  val productImageReads: Reads[ProductImage] = (
    (__ \ "small") .read[String] ~
    (__ \ "medium").read[String] ~
    (__ \ "large") .read[String]
  )(ProductImage.apply _)

  val productImageWrites: Writes[ProductImage] = (
    (__ \ "small") .write[String] ~
    (__ \ "medium").write[String] ~
    (__ \ "large") .write[String]
  ) { pi: ProductImage => (pi.small, pi.medium, pi.large) }

  implicit val productImageFormat: Format[ProductImage] =
    Format(productImageReads, productImageWrites)



  val colorsReads: Reads[Colors] =
    Reads[Colors] { json =>
      traversableReads[Seq, String].reads(json).map(Colors(_))
    }

  val colorsWrites: Writes[Colors] =
    Writes(c => JsArray(c.values.map(Json.toJson(_)).toSeq))

  implicit val colorsFormat: Format[Colors] =
    Format(colorsReads, colorsWrites)



  val sizesReads: Reads[Sizes] =
    Reads[Sizes] { json =>
      traversableReads[Seq, String].reads(json).map(Sizes(_))
    }

  val sizesWrites: Writes[Sizes] =
    Writes(s => JsArray(s.values.map(Json.toJson(_)).toSeq))

  implicit val sizesFormat: Format[Sizes] =
    Format(sizesReads, sizesWrites)



  val brandReads: Reads[Brand] =
    Reads[Brand] { json =>
      StringReads.reads(json).map(Brand(_))
    }

  val brandWrites: Writes[Brand] =
    Writes(b => JsString(b.name))

  implicit val brandFormat: Format[Brand] =
    Format(brandReads, brandWrites)



  val descriptionReads: Reads[Description] =
    Reads[Description] { json =>
      StringReads.reads(json).map(Description(_))
    }

  val descriptionWrites: Writes[Description] =
    Writes(d => JsString(d.text))

  implicit val descriptionFormat: Format[Description] =
    Format(descriptionReads, descriptionWrites)



  val priceReads: Reads[Price] =
    Reads[Price] { json =>
      FloatReads.reads(json).map(Price(_))
    }

  val priceWrites: Writes[Price] =
    Writes(p => JsNumber(p.value))

  implicit val priceFormat: Format[Price] =
    Format(priceReads, priceWrites)



  val apparelFabricReads: Reads[ApparelFabric] =
    Reads[ApparelFabric] { json =>
      StringReads.reads(json).map(ApparelFabric(_))
    }

  val apparelFabricWrites: Writes[ApparelFabric] =
    Writes(f => JsString(f.fabric))

  implicit val apparelFabricFormat: Format[ApparelFabric] =
    Format(apparelFabricReads, apparelFabricWrites)



  val apparelFitReads: Reads[ApparelFit] =
    Reads[ApparelFit] { json =>
      StringReads.reads(json).map(ApparelFit(_))
    }

  val apparelFitWrites: Writes[ApparelFit] =
    Writes(f => JsString(f.fit))

  implicit val apparelFitFormat: Format[ApparelFit] =
    Format(apparelFitReads, apparelFitWrites)



  val apparelStyleReads: Reads[ApparelStyle] =
    Reads[ApparelStyle] { json =>
      StringReads.reads(json).map(ApparelStyle(_))
    }

  val apparelStyleWrites: Writes[ApparelStyle] =
    Writes(s => JsString(s.style))

  implicit val apparelStyleFormat: Format[ApparelStyle] =
    Format(apparelStyleReads, apparelStyleWrites)



  val clothingItemReads: Reads[ClothingItem] = (
    (__ \ "itemId")         .read[CatalogueItemId] ~
    (__ \ "itemType")       .read[ItemType] ~
    (__ \ "itemTypeGroups") .read[ItemTypeGroups] ~
    (__ \ "namedType")      .read[NamedType] ~
    (__ \ "productTitle")   .read[ProductTitle] ~
    (__ \ "productImage")   .read[ProductImage] ~
    (__ \ "colors")         .read[Colors] ~
    (__ \ "sizes")          .read[Sizes] ~
    (__ \ "brand")          .read[Brand] ~
    (__ \ "description")    .read[Description] ~
    (__ \ "price")          .read[Price] ~
    (__ \ "fabric")         .read[ApparelFabric] ~
    (__ \ "fit")            .read[ApparelFit] ~
    (__ \ "style")          .read[ApparelStyle]
  )(ClothingItem.apply _)

  val clothingItemWrites: Writes[ClothingItem] = (
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

  implicit val clothingItemFormat: Format[ClothingItem] =
    Format(clothingItemReads, clothingItemWrites)

}