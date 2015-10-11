package commons.owner

case class StoreName(value: String)
case class StoreWebsite(url: String)

case class StoreInfo(name: StoreName, website: Option[StoreWebsite])

case class Store(storeId: StoreId, info: StoreInfo)