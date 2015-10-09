package commons
package catalogue
package test

import items._
import attributes._

trait SampleCatalogueItems {

  trait WithCatalogueItems {

    val brandId = BrandId(12345L)
    val itemId = CatalogueItemId(123456L)
    val variantId = VariantId(1234567L)

    val title = ProductTitle("mens polo neck tshirt")
    val namedType = NamedType("polo neck tshirt")
    val brand = Brand("Brand name")
    val price = Price(1234.0F)
    val sizes = ClothingSizes(Seq(ClothingSize.S))
    val colors = Colors(Seq(Color.RED))

    val brandItems = Array.ofDim[CatalogueItem](2)

    brandItems(0) =
      MensPoloNeckTShirt.builder.forBrand
        .ids(brandId, itemId, variantId)
        .title(title)
        .namedType(namedType)
        .clothing(brand, price, sizes, colors)
        .build

    brandItems(1) = brandItems(0).asInstanceOf[items.MensPoloNeckTShirt]asClothing

    val storeItems = Array.ofDim[CatalogueItem](5)

    storeItems(0) =
      MensPoloNeckTShirt.builder.forStore.using(brandItems(0))
        .ids(StoreId(1L), itemId, variantId)
        .build

    storeItems(1) =
      MensPoloNeckTShirt.builder.forStore.using(brandItems(0))
        .ids(StoreId(2L), itemId, variantId)
        .title(ProductTitle("new mens polo neck tshirt"))
        .build.asClothing

    storeItems(2) =
      MensPoloNeckTShirt.builder.forStore.using(brandItems(0))
        .ids(StoreId(3L), itemId, variantId)
        .clothing(Brand("Store Brand"), null, null, null)
        .build

    storeItems(3) =
      MensPoloNeckTShirt.builder.forStore.using(brandItems(0))
        .ids(StoreId(4L), itemId, variantId)
        .clothing(null, Price(123.01F), null, null)
        .build

    storeItems(4) = Clothing(storeItems(1).memory.underlying, brandItems(1))

  }

}