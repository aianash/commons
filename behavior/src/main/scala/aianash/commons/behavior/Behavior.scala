package aianash.commons.behavior

import java.net.URL

import org.joda.time.Duration


/////////
// Ids //
/////////

case class InstanceId(val inuuid: Int) extends AnyVal
case class BehaviorId(val bhuuid: Long) extends AnyVal
case class ActionId(val acuuid: Long) extends AnyVal

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
    score  : Float,
    url    : URL)

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
  case class TagDistribution(prob: Map[String, (Double, Double)]) {
    def tags = prob.keys
    def means = prob.map(t => t._1 -> t._2._1)
    def variances = prob.map(t => t._1 -> t._2._2)

    def mean(tag: String) = prob.get(tag).map(_._1)
    def variance(tag: String) = prob.get(tag).map(_._2)
  }

  //
  case class SectionDistribution(prob: Map[Int, (PageSection, Double, Double)]) {
    def sections = prob.values.map(_._1)
    def means = prob.values.map(t => t._1 -> t._2)
    def variance = prob.values.map(t => t._1 -> t._3)

    def mean(sectionId: Int) = prob.get(sectionId).map(_._2)
    def variance(sectionId: Int) = prob.get(sectionId).map(_._3)

    def probability(sectionId: Int) = prob.get(sectionId).map(t => t._2 -> t._3)
  }

  //
  case class Information(
    prior     : TagDistribution,
    posterior : TagDistribution)

  ///////////
  // Story //
  ///////////

  //
  case class Action(category: String, name: String, label: String, stats: Map[String, (String, Long)])
  case class StepUserStats(reached: Long, dropped: Long, continued: Long)

  //
  case class Step(
    durationIntoPage : Duration,
    sections         : SectionDistribution,
    distribution     : TagDistribution,
    userStats        : StepUserStats,
    actions          : Set[Action])

  //
  case class Story(
    information : Information,
    steps       : Seq[Step])
}