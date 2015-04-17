include 'common.thrift'

namespace java com.goshoplane.gluon.service

exception GluonException {
  1: string message;
}

service Gluon {
  bool publish(1:common.SerializedCatalogueItem serializedCatalogueItem) throws (1:GluonException gex);
}