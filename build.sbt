import Dependencies._

val mainClassName = Some("net.michaelripley.elite_etl.ExtractTransformLoad")

lazy val root = (project in file(".")).
  settings(
    inThisBuild(
      List(
        scalaVersion := "2.12.6"
      )
    ),
    organization := "net.michaelripley",
    name := "elite-etl",
    version := "0.1.0",
    resolvers ++= extraResolvers,
    libraryDependencies ++= testLibraries,
    libraryDependencies ++= dependencies,
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    mainClass in(Compile, run) := mainClassName,
    mainClass in(Compile, packageBin) := mainClassName,
    mainClass in assembly := mainClassName
  )
