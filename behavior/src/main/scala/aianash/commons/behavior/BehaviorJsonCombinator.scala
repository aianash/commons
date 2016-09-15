package aianash.commons.behavior

import java.net.URL

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.joda.time.Duration

//
trait BehaviorJsonCombinator {
  import Behavior._

  implicit val distributionParamsFormat: Format[DistributionParams] = (
    (__ \ "mean").format[String] and
    (__ \ "var").format[String]
  ) (
    (mean, variance) => DistributionParams(mean.toDouble, variance.toDouble),
    (params: DistributionParams) => (params.mean.toString, params.variance.toString)
  )

  implicit val informationFormat: Format[Information] = (
    (__ \ "prior").format[Map[String, DistributionParams]] and
    (__ \ "posterior").format[Map[String, DistributionParams]]
  ) (
    (prior, posterior) => Information(TagDistribution(prior), TagDistribution(posterior)),
    (information: Information) => (information.prior.prob, information.posterior.prob)
  )

  implicit val timelineStatsFormat: Format[TimelineStats] = (
    (__ \ "reach").format[String] and
    (__ \ "drop").format[String]
  ) (
    (reach, drop) => TimelineStats(reach.toLong, drop.toLong),
    (stats: TimelineStats) => stats.reach.toString -> stats.drop.toString
  )

  implicit val actionFormat: Format[Action] = (
    (__ \ "category").format[String] and
    (__ \ "name").format[String] and
    (__ \ "label").format[String] and
    (__ \ "stats").format[Map[String, Map[String, String]]]
  ) (
    (category, name, label, stats) => {
      Action( category,
              name,
              label,
              stats.mapValues { v =>
                v.toSeq.map(ss => ss._1 -> ss._2.toLong)
              }
            )
    },
    (action: Action) => {
      import action._
      (
        category,
        name,
        label,
        stats.mapValues { v =>
          v.map(sl => sl._1 -> sl._2.toString).toMap
        }
      )
    }
  )

  private implicit val sectionDistributionFormat: Format[(PageSection, DistributionParams)] = (
    (__ \ "sectionId").format[String] and
    (__ \ "name").format[String] and
    (__ \ "mean").format[String] and
    (__ \ "var").format[String]
  ) (
    (sectionId, name, mean, variance) =>
      PageSection(sectionId.toInt, name) -> DistributionParams(mean.toDouble, variance.toDouble),
    (sd: (PageSection, DistributionParams)) =>
      (sd._1.sectionId.toString, sd._1.name, sd._2.mean.toString, sd._2.variance.toString)
  )

  implicit val timelineFormat: Format[Timeline] = (
    (__ \ "durationIntoPage").format[String] and
    (__ \ "sections").format[Map[String, (PageSection, DistributionParams)]] and
    (__ \ "tags").format[Map[String, DistributionParams]] and
    (__ \ "stats").format[TimelineStats] and
    (__ \ "actions").format[Set[Action]]
  ) (
    (durationIntoPage, sections, tags, stats, actions) =>
      Timeline(new Duration(durationIntoPage),
               SectionDistribution(
                 sections.map(kv => kv._1.toInt -> kv._2)),
               TagDistribution(tags),
               stats,
               actions),
    (timeline: Timeline) => {
      import timeline._
      (
        durationIntoPage.getMillis.toString,
        sections.prob.map(kv => kv._1.toString -> kv._2),
        tags.prob,
        stats, actions
      )
    }
  )

  implicit val storyFormat: Format[Story] = (
    (__ \ "information").format[Information] and
    (__ \ "timeline").format[IndexedSeq[Timeline]]
  ) (
    (information, timeline) => Story(information, timeline),
    (story: Story) => (story.information, story.timeline)
  )

  implicit val referralFormat: Format[Referral] = (
    (__ \ "pageId").format[String] and
    (__ \ "name").format[String] and
    (__ \ "score").format[String] and
    (__ \ "url").format[String]
  ) (
    (pageId, name, score, url) => Referral(pageId.toLong, name, score.toFloat, new URL(url)),
    (referral: Referral) => {
      import referral._
      (pageId.toString, name, score.toString, url.toString)
    }
  )

  implicit val statsFormat: Format[Stats] = (
    (__ \ "pageViews").format[String] and
    (__ \ "totalVisitors").format[String] and
    (__ \ "newVisitors").format[String] and
    (__ \ "avgDwellTime").format[String] and
    (__ \ "previousPages").format[Seq[Referral]] and
    (__ \ "nextPages").format[Seq[Referral]]
  ) (
    (pageViews, totalVisitors, newVisitors, avgDwellTime, previousPages, nextPages) =>
      Stats(
        PageViews(pageViews.toLong),
        Visitors(totalVisitors.toLong),
        Visitors(newVisitors.toLong),
        new Duration(avgDwellTime.toLong),
        previousPages,
        nextPages),
    (stats: Stats) => {
      import stats._
      (
        pageViews.count.toString,
        totalVisitors.count.toString,
        newVisitors.count.toString,
        avgDwellTime.getMillis.toString,
        previousPages,
        nextPages
      )
    }
  )

  implicit val behaviorFormat: Format[Behavior] = (
    (__ \ "behaviorId").format[String] and
    (__ \ "name").format[String]
  ) (
    (behaviorId, name) => Behavior(BehaviorId(behaviorId.toLong), name),
    (behavior: Behavior) => (behavior.behaviorId.bhuuid.toString, behavior.name)
  )
}