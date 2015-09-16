import AssemblyKeys._

name := "twitterstream-clients"

version := "1.0"

sbtVersion := "0.13.8"

resolvers ++= Seq(
  "Apache Staging" at "https://repository.apache.org/content/groups/staging/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.12",
  "org.apache.spark" %% "spark-core" % "1.4.1" % "provided"
)

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")            => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")        => MergeStrategy.discard
  case "log4j.properties"                                    => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/")   => MergeStrategy.filterDistinctLines
  case "reference.conf"                                      => MergeStrategy.concat
  case _                                                     => MergeStrategy.first
}

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator