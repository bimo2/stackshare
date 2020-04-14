name := "stackshare"
organization := "io.bimo2"
version := "1.0"
scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += "com.typesafe.play" %% "play" % "2.8.1"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.0"
