import ReleaseTransformations._

// Constants //

val projectName = "shash"
val scala211    = "2.11.12"
val scala212    = "2.12.10"

// Lazy

lazy val scalaVersions = List(scala211, scala212)

// Groups //

val openJdkJmhG = "org.openjdk.jmh"
val scalacheckG = "org.scalacheck"
val scodecG     = "org.scodec"
val typelevelG  = "org.typelevel"

// Artifacts //

val catsEffectA             = "cats-effect"
val jmhCoreA                = "jmh-core"
val jmhGeneratorAnnProcessA = "jmh-generator-annprocess"
val jmhGeneratorAsmA        = "jmh-generator-asm"
val scalacheckA             = "scalacheck"
val scodecBitsA             = "scodec-bits"

// Versions //

val catsEffectV = "1.4.0"
val jmhV        = "1.21"
val scalacheckV = "1.14.0"
val scodecBitsV = "1.1.12"

// GAVs //

lazy val catsEffect             = typelevelG  %% catsEffectA            % catsEffectV
lazy val jmhCore                = openJdkJmhG % jmhCoreA                % jmhV
lazy val jmhGeneratorAnnProcess = openJdkJmhG % jmhGeneratorAnnProcessA % jmhV
lazy val jmhGeneratorAsm        = openJdkJmhG % jmhGeneratorAsmA        % jmhV
lazy val scalacheck             = scalacheckG %% scalacheckA            % scalacheckV
lazy val scodecBits             = scodecG     %% scodecBitsA            % scodecBitsV

// ThisBuild Scoped Settings //

ThisBuild / organization       := "io.isomarcte"
ThisBuild / scalaVersion       := scala212
ThisBuild / scalacOptions      += "-target:jvm-1.8"
ThisBuild / scalacOptions      -= "-Xfuture"
ThisBuild / javacOptions       ++= Seq("-source", "1.8", "-target", "1.8")

// General Configuration //
lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/isomarcte/shash")),
  licenses := Seq("BSD3" -> url("https://opensource.org/licenses/BSD-3-Clause")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/isomarcte/shash"),
      "scm:git:git@github.com:isomarcte/shash.git"
    )
  ),
  developers := List(
    Developer("isomarcte", "David Strawn", "isomarcte@gmail.com", url("https://github.com/isomarcte"))
  ),
  credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  crossScalaVersions := scalaVersions
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
// Root Project //

lazy val root = (project in file(".")).settings(
  name := projectName,
  skip in publish := true,
  scalacOptions -= "-Xfuture"
).aggregate(cats).settings(publishSettings: _*)

// Projects //

lazy val cats = project.settings(
  name := s"$projectName-cats",
  libraryDependencies ++= Seq(
    catsEffect,
    scodecBits
  ),
  scalacOptions -= "-Xfuture"
).settings(publishSettings: _*)

lazy val jmh = project.settings(
  name := s"$projectName-jmh",
  libraryDependencies ++= Seq(
    jmhCore
  ),
  mainClass in assembly := Some("org.openjdk.jmh.Main"),
  skip in publish := true
).dependsOn(
  cats
).enablePlugins(
  JmhPlugin
)
