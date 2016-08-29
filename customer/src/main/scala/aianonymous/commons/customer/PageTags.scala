package aianonymous.commons.customer

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class PageTags(
  tokenId   : Long,
  pageId    : Long,
  sectionId : Int,
  tags      : Set[String]
  )


trait PageTagsJsonCombinator {

  protected implicit val pageTagsFormat: Format[PageTags] = (
    (__ \ "tid").format[Long] and
    (__ \ "pid").format[Long] and
    (__ \ "sid").format[Int] and
    (__ \ "tags").format[Set[String]]
  ) ((tid, pid, sid, tags) =>
      PageTags(tid, pid, sid, tags),
      (a: PageTags) => (a.tokenId, a.pageId, a.sectionId, a.tags)
  )

}