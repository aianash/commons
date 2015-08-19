package commons.catalogue.test

import commons.catalogue._
import commons.catalogue.attributes._
import commons.catalogue.items.MensPoloNeckTShirt

import org.scalatest._

class MensPoloNeckTShirtSpec extends UnitSpec {

  "MensPoloNeckTShirt" should {
    "be able to create a brand item" in {
      Given("all the attributes")
      val brandId = BrandId(12345L)
      val itemId = CatalogueItemId(123456L)
      val variantId = VariantId(1234567L)

      val title = ProductTitle("mens polo neck tshirt")
      val namedType = NamedType("polo neck tshirt")
      val brand = Brand("Brand name")
      val price = Price(1234.0F)
      val sizes = ClothingSizes(Seq(ClothingSize.S))
      val colors = Colors(Seq(Color.RED))

      When("the item is created using builder")
      val item =
        MensPoloNeckTShirt.builder.forBrand
          .ids(brandId, itemId, variantId)
          .title(title)
          .namedType(namedType)
          .clothing(brand, price, sizes, colors)
          .build

      Then("it should return correct ids")
      assert(item.ownerId equals brandId)
      assert(item.itemId equals itemId)
      assert(item.variantId equals variantId)

      And("it should return correct product title and named type")
      assert(item.productTitle equals title)
      assert(item.namedType equals namedType)

      And("it should return correct clothing attributes")
      assert(item.brand equals brand)
      assert(item.price equals price)

      info("Then it was able to correctly build MensPoloNeckTShirt Brand Item")
    }

    "be able to create a store item" in {
      Given("a brand item")
      val brandId = BrandId(12345L)
      val storeId = StoreId(22345L)
      val itemId = CatalogueItemId(123456L)
      val variantId = VariantId(1234567L)

      val title = ProductTitle("mens polo neck tshirt")
      val namedType = NamedType("polo neck tshirt")
      val brand = Brand("Brand name")
      val price = Price(1234.0F)
      val sizes = ClothingSizes(Seq(ClothingSize.S))
      val colors = Colors(Seq(Color.RED))

      val brandItem =
        MensPoloNeckTShirt.builder.forBrand
          .ids(brandId, itemId, variantId)
          .title(title)
          .namedType(namedType)
          .clothing(brand, price, sizes, colors)
          .build

      When("the store item is created using builder and inheriting all the attributes")
      val item =
        MensPoloNeckTShirt.builder.forStore.using(brandItem)
          .ids(storeId, itemId, variantId)
          .build

      Then("it should return correct ids")
      assert(item.ownerId equals storeId)
      assert(item.itemId equals itemId)
      assert(item.variantId equals variantId)

      And("it should return correct product title and named type")
      assert(item.productTitle equals title)
      assert(item.namedType equals namedType)

      And("it should return correct clothing attributes")
      assert(item.brand equals brand)
      assert(item.price equals price)

      info("Then it was able to correctly build MensPoloNeckTShirt Store Item using a Brand Item")
    }
  }

}