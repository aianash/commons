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

import com.goshoplane.sbt.standard.libraries.StandardLibraries


object CommonsBuild extends Build with CommonsLibraries with StandardLibraries {

  def sharedSettings = Seq(
    organization := "com.goshoplane",
    version := "0.0.1",
    scalaVersion := Version.scala,
    crossScalaVersions := Seq(Version.scala, "2.10.4"),
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps", "-language:reflectiveCalls", "-Yinline-warnings", "-encoding", "utf8"),
    retrieveManaged := true,

    fork := true,
    javaOptions += "-Xmx2500M",

    resolvers ++= StandardResolvers,

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
  ) aggregate (core, catalogue, microservice)



  lazy val core = Project(
    id = "commons-core",
    base = file("core"),
    settings = Project.defaultSettings ++
      sharedSettings ++
      ScroogeSBT.newSettings
  ).settings(
    name := "commons-core",

    libraryDependencies ++= Seq(
    ) ++ Libs.scalaz
      ++ Libs.akka
      ++ Libs.phantom
      ++ Libs.fastutil
      ++ CommonsLibs.libThrift
      ++ CommonsLibs.finagleThrift
      ++ CommonsLibs.scroogeCore
  )


  lazy val catalogue = Project(
    id = "commons-catalogue",
    base = file("catalogue"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "commons-catalogue",

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-testkit" % Version.akka
    ) ++ Libs.scalatest
      ++ Libs.fastutil
  ).dependsOn(core)


  lazy val microservice = Project(
    id = "commons-microservice",
    base = file("microservice"),
    settings = Project.defaultSettings ++
      SbtMultiJvm.multiJvmSettings ++
      sharedSettings
  ).settings(
    name := "commons-microservice",

    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),

    jvmOptions in MultiJvm := Seq("-Xmx256M"),

    parallelExecution in Test := false,

    // make sure that MultiJvm tests are executed by the default test target,
    // and combine the results from ordinary test and multi-jvm tests
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    },

    libraryDependencies ++= Seq(
    ) ++ Libs.akka
      ++ Libs.akkaCluster
      ++ Libs.curator
      ++ Libs.curatorTest
      ++ Libs.scalatest
      ++ Libs.akkaMultiNodeTestkit
  ).configs(MultiJvm)

}