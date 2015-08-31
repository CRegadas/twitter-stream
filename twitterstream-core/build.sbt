import AssemblyKeys._

name := "twitterstream-core"

version := "1.0"

sbtVersion := "0.13.8"

resolvers ++= Seq(
  "Apache Staging" at "https://repository.apache.org/content/groups/staging/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq()

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")            => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")        => MergeStrategy.discard
  case "log4j.properties"                                    => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/")   => MergeStrategy.filterDistinctLines
  case "reference.conf"                                      => MergeStrategy.concat
  case _                                                     => MergeStrategy.first
}
