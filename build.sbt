import AssemblyKeys._

name := "twitter-stream"

version := "1.0"

sbtVersion := "0.13.8"

resolvers ++= Seq(
  "Apache Staging" at "https://repository.apache.org/content/groups/staging/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" %  "0.8.2.1",
  "org.twitter4j" % "twitter4j-stream" % "3.0.3",
  "net.debasishg" %% "redisclient" % "3.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.3.0-M1",
  "junit" % "junit" % "4.12" % "test",
  "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1",
  //"org.apache.spark" %% "spark-streaming-twitter" % "1.4.1",
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.12",
  "com.amazonaws" % "aws-java-sdk" % "1.8.0",
  "org.log4s" %% "log4s" % "1.1.5",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
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
