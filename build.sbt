name := "stackshare"
organization := "io.bimo2"
version := "1.0"
scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += "com.typesafe.play" %% "play" % "2.8.1"
