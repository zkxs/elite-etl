import sbt._

object Dependencies {

  lazy val dependencies = Seq(
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.slf4j" % "slf4j-jdk14" % "1.7.25",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.6",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.6",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6",
    "org.postgresql" % "postgresql" % "42.2.5"
  )

  lazy val testLibraries: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.3"
  ).map(_ % Test)

  lazy val extraResolvers = Seq(
    Resolver.jcenterRepo
  )

}
