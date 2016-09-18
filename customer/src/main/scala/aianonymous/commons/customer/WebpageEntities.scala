package aianonymous.commons.customer

import aianonymous.commons.core.PageURL

case class Domain(
  tokenId : Long,
  name    : String
  )

case class WebPage(
  tokenId : Long,
  pageId  : Long,
  url     : PageURL,
  name    : String
  )