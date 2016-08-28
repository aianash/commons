package aianonymous.commons.customer

sealed trait Settings

case class PageTags(
  tokenId   : Long,
  pageId    : Long,
  sectionId : Int,
  tags      : Set[String]
  ) extends Settings