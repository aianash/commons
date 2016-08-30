package aianonymous.commons.customer

case class Domain(
  tokenId : Long,
  name    : String
  )

case class PageURL(
  tokenId : Long,
  pageId  : Long,
  url     : String
  )