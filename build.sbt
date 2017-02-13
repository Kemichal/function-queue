name := "function-queue"
description := "Run Scala functions in a queue"

organization := "com.kemichal"

version := "1.0.0"

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.11.8", "2.12.1")
homepage := Some(url(s"https://github.com/kemichal/function-queue"))
licenses += "MIT" -> url(
  "https://github.com/kemichal/function-queue/blob/master/LICENSE")
scmInfo := Some(
  ScmInfo(url("https://github.com/kemichal/function-queue"),
          "git@github.com:kemichal/function-queue.git"))

releaseCrossBuild := true
publishMavenStyle := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging)

pomExtra :=
  <developers>
    <developer>
      <id>Kemichal</id>
      <name>Robert Andersson</name>
      <url>https://github.com/kemichal</url>
    </developer>
  </developers>

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  publishArtifacts
)
