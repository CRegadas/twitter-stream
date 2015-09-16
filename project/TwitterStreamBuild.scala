import play.sbt.PlayScala
import sbt._

object TwitterStreamBuild extends Build {
  lazy val root = Project(id = "twitterstream",
    base = file(".")) aggregate(core, analytics, streams,  clients)

  lazy val core = Project(id = "twitterstream-core",
    base = file("twitterstream-core"))

  lazy val analytics = Project(id = "twitterstream-analytics",
    base = file("twitterstream-analytics")) dependsOn(core)

  lazy val streams = Project(id = "twitterstream-streams",
    base = file("twitterstream-streams")) dependsOn(core)

  lazy val clients = Project(id = "twitterstream-clients",
    base = file("twitterstream-clients")).enablePlugins(PlayScala) dependsOn(core, analytics)

}
