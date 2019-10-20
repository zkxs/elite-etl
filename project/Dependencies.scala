import sbt._

object Dependencies {

  val slf4jVersion = "1.7.28"
  val jacksonVersion = "2.10.0"

  lazy val dependencies = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "slf4j-jdk14" % slf4jVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
    "org.postgresql" % "postgresql" % "42.2.8"
  )

  lazy val testLibraries: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.0-M1"
  ).map(_ % Test)

  lazy val extraResolvers = Seq(
    Resolver.jcenterRepo
  )

}
