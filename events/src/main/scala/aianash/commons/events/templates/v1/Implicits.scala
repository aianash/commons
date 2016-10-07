package aianash.commons.events.templates.v1

object Implicits {
  implicit val pageFragmentViewTemplate = new PageFragmentViewTemplate
  implicit val sectionViewTemplate = new SectionViewTemplate
  implicit val mousePathTemplate = new MousePathTemplate
  implicit val scanningTemplate = new ScanningTemplate
  implicit val actionTemplate = new ActionTemplate
  implicit val locationTemplate = new LocationTemplate
  implicit val webPageTemplate = new WebPageTemplate
  implicit val appTemplate = new AppTemplate
}
