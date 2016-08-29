package aianonymous.commons.customer

case class PageTags(
  tokenId   : Long,
  pageId    : Long,
  sectionId : Int,
  tags      : Set[String]
  )
