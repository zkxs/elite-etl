import Dependencies._

val mainClassName = Some("net.michaelripley.elite_etl.ExtractTransformLoad")

lazy val root = (project in file(".")).
  settings(
    inThisBuild(
      List(
        scalaVersion := "2.13.1"
      )
    ),
    organization := "net.michaelripley",
    name := "elite-etl",
    version := "0.5.0",
    resolvers ++= extraResolvers,
    libraryDependencies ++= testLibraries,
    libraryDependencies ++= dependencies,
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    mainClass in(Compile, run) := mainClassName,
    mainClass in(Compile, packageBin) := mainClassName,
    mainClass in assembly := mainClassName,
    assemblyMergeStrategy in assembly := {
      case "module-info.class" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
