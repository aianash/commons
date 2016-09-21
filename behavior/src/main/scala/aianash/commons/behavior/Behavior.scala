package aianash.commons.behavior

import org.joda.time.Duration

import aianonymous.commons.core.PageURL


/////////
// Ids //
/////////

case class InstanceId(val inuuid: Int) extends AnyVal
case class BehaviorId(val bhuuid: Long) extends AnyVal

//
case class Behavior(
  behaviorId : BehaviorId,
  name       : String)

//
object Behavior {

  // [TODO] move some where else
  case class PageSection(sectionId: Int, name: String)

  ///////////
  // Stats //
  ///////////

  //
  case class PageViews(count: Long) extends AnyVal
  case class Visitors(count: Long) extends AnyVal

  //
  case class Referral(
    pageId : Long,
    name   : String,
    count  : Long,
    url    : PageURL)

  //
  case class Stats(
    pageViews     : PageViews,
    totalVisitors : Visitors,
    newVisitors   : Visitors,
    avgDwellTime  : Duration,
    previousPages : Seq[Referral],
    nextPages     : Seq[Referral])

  /////////////////
  // Information //
  /////////////////

  //
  case class DistributionParams(mean: Double, variance: Double)

  //
  case class TagDistribution(prob: Map[String, DistributionParams]) {
    def tags = prob.keys
    def means = prob.map(t => t._1 -> t._2.mean)
    def variances = prob.map(t => t._1 -> t._2.variance)

    def mean(tag: String) = prob.get(tag).map(_.mean)
    def variance(tag: String) = prob.get(tag).map(_.variance)
  }

  //
  case class SectionDistribution(prob: Map[Int, (PageSection, DistributionParams)]) {
    def sections = prob.values.map(_._1)
    def means = prob.values.map(t => t._1 -> t._2.mean)
    def variance = prob.values.map(t => t._1 -> t._2.variance)

    def mean(sectionId: Int) = prob.get(sectionId).map(_._2.mean)
    def variance(sectionId: Int) = prob.get(sectionId).map(_._2.variance)

    def probability(sectionId: Int) = prob.get(sectionId).map(t => t._2)
  }

  //
  case class Information(
    prior     : TagDistribution,
    posterior : TagDistribution)

  ///////////
  // Story //
  ///////////

  //
  case class Action(category: String, name: String, label: String, stats: Map[String, Seq[(String, Long)]])
  case class TimelineStats(reach: Long, drop: Long)

  //
  case class TimelineEvent(
    durationIntoPage : Duration,
    sections         : SectionDistribution,
    tags             : TagDistribution,
    stats            : TimelineStats,
    actions          : Set[Action])

  //
  case class Story(
    information : Information,
    timeline    : IndexedSeq[TimelineEvent])
}