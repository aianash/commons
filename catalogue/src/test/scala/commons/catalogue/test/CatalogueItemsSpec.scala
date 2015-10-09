package commons
package catalogue
package test

import attributes._
import collection._
import items.{MensPoloNeckTShirt, Clothing}

import org.scalatest._

class CatalogueItemsSpec extends UnitSpec with SampleCatalogueItems {

  "CatalogueItems" should {

    "create, serialize and deserialize" in new WithCatalogueItems {

      val items = CatalogueItems(storeItems.toSeq)
      val binary = items.toBinary

      val recoveredItems = CatalogueItems(binary)
      assert(recoveredItems equals items)
      // recoveredItems.toBinary
    }
  }

}