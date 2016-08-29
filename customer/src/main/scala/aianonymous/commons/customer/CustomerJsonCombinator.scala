package aianonymous.commons.customer

import play.api.libs.json._
import play.api.libs.functional.syntax._


trait CustomerJsonCombinator {

   implicit val pageTagsFormat: Format[PageTags] = (
    (__ \ "token_id").format[Long] and
    (__ \ "pgae_id").format[Long] and
    (__ \ "section_id").format[Int] and
    (__ \ "tags").format[Set[String]]
  ) ((tid, pid, sid, tags) =>
      PageTags(tid, pid, sid, tags),
      (a: PageTags) => (a.tokenId, a.pageId, a.sectionId, a.tags)
  )

   implicit val domainFormat: Format[Domain] = (
    (__ \ "token_id").format[String] and
    (__ \ "domain").format[String]
  ) ((tid, domain) =>
      Domain(tid.toLong, domain),
      (a: Domain) => (a.tokenId.toString, a.name)
  )

}