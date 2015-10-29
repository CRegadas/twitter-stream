package org.twitterstream

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark._
import org.twitterstream.Settings.{Spark => Config}
import org.twitterstream.analytics._
import org.twitterstream.core._

import scala.concurrent._
import scala.concurrent.duration._


object ClientApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-client")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  implicit val timeout = Timeout(5.seconds)

  val conf = new SparkConf()
    .setMaster(Config.master)
    .set("spark.cassandra.connection.host", Config.cassandraHost)
    .set("spark.cores.max", "4")
    .setAppName("ts-client-app")
    .setJars(Seq(Config.jars))

  val ssc = new SparkContext(conf)
  val wordCount = system.actorOf(HashtagCountActor.props(ssc), "hashtag-count")

  val future = wordCount ? GetTopKHashtagCount(5)
  future onSuccess {
    case TopKHashtagCount(top) => println(top)
  }
}





































