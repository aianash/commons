include 'common.thrift'

namespace java com.goshoplane.cassie.service
namespace js cassie.service

exception CassieException {
  1: string message;
}

service Cassie {
  list<common.SerializedCatalogueItem> getStoreCatalogue(1:common.StoreId storeId) throws (1:CassieException cex);
  list<common.SerializedCatalogueItem> getStoreCatalogueForType(1:common.StoreId storeId, 2:list<common.ItemType> itemTypes) throws (1:CassieException cex);
  list<common.SerializedCatalogueItem> getCatalogueItems(1:list<common.CatalogueItemId> catalogueItemIds, 2:common.CatalogeItemDetailType detailType) throws (1:CassieException cex);

  common.StoreId createOrUpdateStore(1:common.StoreType storeType, 2:common.StoreInfo info) throws (1:CassieException cex);
  common.Store getStore(1:common.StoreId storeId, 2:list<common.StoreInfoField> fields) throws (1:CassieException cex);

}