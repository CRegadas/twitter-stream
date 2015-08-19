package org.twitterstream

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.{ExecutorService, Executors}

import org.twitterstream.core._
import org.twitterstream.analytics._

import org.apache.spark._
import org.apache.spark.streaming._


object ClientApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-client")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  implicit val timeout = Timeout(5.seconds)

  val conf = new SparkConf()
    .setMaster("spark://twister.lan:7077")
    .set("spark.cassandra.connection.host", "192.168.59.103")
    .set("spark.cores.max", "4")
    .setAppName("ts-client-app")
    .setJars(Seq("/Users/regadas/projects/twitter-stream/twitterstream-analytics/target/scala-2.10/twitterstream-analytics-assembly-1.0.jar"))

  val ssc = new SparkContext(conf)
  val wordCount = system.actorOf(WordCountActor.props(ssc), "word-count")

  val future = wordCount ? GetTopKWordCount("RT", 10)
  future onSuccess {
    case TopKWordCount(word, top) => println(top)
  }
}






































