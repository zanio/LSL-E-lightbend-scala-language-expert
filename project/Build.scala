import sbt._
import sbt.Keys._

object CompileOptions {

  lazy val compileOptions = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-encoding", "UTF-8"
  )
}

object CommonSettings {
  lazy val commonSettings = Seq(
    organization := "com.lightbend.training",
    version := "3.1.0",
    libraryDependencies ++= Dependencies.dependencies,
    scalacOptions ++= CompileOptions.compileOptions,
  )
}

object Version {
  lazy val scalaVersion          = "2.13.10"
  lazy val scalaParsersVersion   = "2.1.0"
  lazy val scalaTestVersion      = "3.2.10"
}

object Dependencies {
  import Version._

  lazy val dependencies = Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % scalaParsersVersion,
    "org.scala-lang"          % "scala-reflect"            % scalaVersion,
    "org.scalatest"          %% "scalatest"                % scalaTestVersion,
  )
}
