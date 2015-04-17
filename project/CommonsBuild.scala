import sbt._
import sbt.Classpaths.publishTask
import Keys._

import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{ MultiJvm, extraOptions, jvmOptions, scalatestOptions, multiNodeExecuteTests, multiNodeJavaName, multiNodeHostsFileName, multiNodeTargetDirName, multiTestOptions }

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import com.typesafe.sbt.SbtStartScript

import sbtassembly.AssemblyPlugin.autoImport._

import com.twitter.scrooge.ScroogeSBT

object CommonsBuild extends Build with Libraries {

  def sharedSettings = Seq(
    organization := "com.goshoplane",
    version := "0.0.1",
    scalaVersion := Version.scala,
    crossScalaVersions := Seq(Version.scala, "2.11.4"),
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps", "-language:reflectiveCalls", "-Yinline-warnings", "-encoding", "utf8"),
    retrieveManaged := true,

    fork := true,
    javaOptions += "-Xmx2500M",

    resolvers ++= Seq(
      "ReaderDeck Releases"    at "http://repo.readerdeck.com/artifactory/readerdeck-releases",
      "anormcypher"            at "http://repo.anormcypher.org/",
      "Akka Repository"        at "http://repo.akka.io/releases",
      "Spray Repository"       at "http://repo.spray.io/",
      "twitter-repo"           at "http://maven.twttr.com",
      "Typesafe Repository"    at "http://repo.typesafe.com/typesafe/releases/",
      "Websudos releases"      at "http://maven.websudos.co.uk/ext-release-local",
      "Websudos snapshots"     at "http://maven.websudos.co.uk/ext-snapshot-local",
      "Sonatype repo"          at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Sonatype releases"      at "https://oss.sonatype.org/content/repositories/releases",
      "Sonatype snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype staging"       at "http://oss.sonatype.org/content/repositories/staging",
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
    ),

    publishMavenStyle := true
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings


  lazy val commons = Project(
    id = "commons",
    base = file("."),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
  ) aggregate (core)



  lazy val core = Project(
    id = "commons-core",
    base = file("core"),
    settings = Project.defaultSettings ++
      sharedSettings ++
      SbtStartScript.startScriptForClassesSettings ++
      ScroogeSBT.newSettings
  ).settings(
    name := "commons-core",

    libraryDependencies ++= Seq(
    ) ++ Libs.scalaz
      ++ Libs.scroogeCore
      ++ Libs.finagleThrift
      ++ Libs.libThrift
      ++ Libs.akka
      ++ Libs.scaldi
      ++ Libs.fastutil
  )
}