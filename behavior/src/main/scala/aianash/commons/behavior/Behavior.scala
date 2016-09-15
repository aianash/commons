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

  ///////////
  // Stats //
  ///////////

  //
  case class PageViews(count: Long) extends AnyVal
  case class Visitors(count: Long) extends AnyVal

  //
  case class Referral(
    pageId : Long,
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
  case class Distribution(prob: Map[String, (Double, Double)]) {
    def tags = prob.keys
    def means = prob.map(t => t._1 -> t._2._1)
    def variances = prob.map(t => t._1 -> t._2._2)

    def mean(tag: String) = prob.get(tag).map(_._1)
    def variance(tag: String) = prob.get(tag).map(_._2)
  }

  //
  case class Information(
    prior     : Distribution,
    posterior : Distribution)

  ///////////
  // Story //
  ///////////

  // [TODO] move some where else
  case class PageSection(sectionId: Int, name: String)

  //
  case class Action(category: String, name: String, label: String, stats: Map[String, (String, Long)])
  case class StepUserStats(reached: Long, dropped: Long, continued: Long)

  //
  case class Step(
    durationIntoPage : Duration,
    section          : PageSection,
    distribution     : Distribution,
    userStats        : StepUserStats,
    actions          : Set[Action])

  //
  case class Story(
    information : Information,
    steps       : Seq[Step])
}