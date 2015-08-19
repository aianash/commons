import sbt._
import Keys._

/**
 * This includes libraries that are
 * not present in Standard Libraries
 */
trait CommonsLibraries {
  private object CommonsVersion {
    val scrooge         = "3.17.0"
    val libThrift       = "0.9.2"
    val finagle         = "6.24.0"
  }


  object CommonsLibs {

    val finagleThrift = Seq (
      "com.twitter" %% "finagle-thrift" % CommonsVersion.finagle)

    val libThrift = Seq (
      "org.apache.thrift" % "libthrift" % CommonsVersion.libThrift intransitive)

    val scroogeCore = Seq (
      "com.twitter" %% "scrooge-core"   % CommonsVersion.scrooge)
  }

}