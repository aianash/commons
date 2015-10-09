package commons
package catalogue
package test

import akka.testkit._
import akka.serialization._
import akka.actor.ActorSystem

import com.typesafe.config.ConfigFactory

import attributes._
import items._
import collection._
import serialization._

class CatalogueItemAkkaSerializerSpec(_system: ActorSystem) extends AkkaUnitSpec(_system) with SampleCatalogueItems {

  def this() = {
    this(ActorSystem("catalogue-serializer-spec", ConfigFactory.parseString("""
      akka {
        actor {
          serializers {
            catalogue = "commons.catalogue.serialization.CatalogueItemAkkaSerializer"
          }

          serialization-bindings {
            "commons.catalogue.CatalogueItem" = catalogue
            "commons.catalogue.collection.CatalogueItems" = catalogue
          }
        }
      }
    """)))
  }


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "demonstrate configuration of serialization-bindings" in {
    SerializationExtension(system).serializerFor(classOf[CatalogueItem]).getClass should be(classOf[CatalogueItemAkkaSerializer])
    SerializationExtension(system).serializerFor(classOf[CatalogueItems]).getClass should be(classOf[CatalogueItemAkkaSerializer])
  }

  "demonstrate serialization and deserialization for brand catalogue item" in new WithCatalogueItems {
    Then("we can serialize the catalogue item")
    val bytes = SerializationExtension(system).serialize(brandItems(0)).get

    And("we can also desirialize to catalogue item")
    val item = SerializationExtension(system).deserialize(bytes, classOf[CatalogueItem]).get
    assert(item equals brandItems(0))
  }

  "demonstrate serialization and deserialization for store catalogue item" in new WithCatalogueItems {
    val bytes = SerializationExtension(system).serialize(storeItems(3)).get
    val item = SerializationExtension(system).deserialize(bytes, classOf[CatalogueItem]).get
    assert(item equals storeItems(3))
  }

  "demonstrate serialization and deserialization for catalogue items" in new WithCatalogueItems {
    val items = CatalogueItems(storeItems.toSeq)
    val bytes = SerializationExtension(system).serialize(items).get
    val recoveredItems = SerializationExtension(system).deserialize(bytes, classOf[CatalogueItems]).get
    recoveredItems.items foreach { ci =>
      val item = ci.asInstanceOf[catalogue.items.Clothing]
      println(s"""
        id = ${item.itemId}
        ownerId = ${item.ownerId}
        title = ${item.productTitle}
        brand = ${item.brand}
        price = ${item.price}
      """)
    }
    assert(items equals recoveredItems)
  }

}