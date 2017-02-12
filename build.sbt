name := "function-queue"

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.11.8", "2.12.1")

releaseCrossBuild := true
publishMavenStyle := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  publishArtifacts
)
