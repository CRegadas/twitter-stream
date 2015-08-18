package org.twitterstream

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.{ExecutorService, Executors}

import org.twitterstream.Settings._
import org.twitterstream.store._
import org.twitterstream.stream._
import org.twitterstream.analytics._
import org.twitterstream.utils._
import org.twitterstream.core._

import org.apache.spark._
import org.apache.spark.streaming._


object AnalyticsApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-analytics")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  val conf = new SparkConf()
    .set("spark.cores.max", "4")
    .set("spark.cassandra.connection.host", "192.168.59.103")
    .setAppName("ts-analytics")

  val ssc = new StreamingContext(conf, Milliseconds(400))
  system.actorOf(Supervisor.props(ssc), "supervisor")

  system.awaitTermination
}

object StreamApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-stream")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  system.actorOf(Props[TwitterStream], "twitterstream")
  system.actorOf(KafkaPublisher.props(Kafka.tweetsTopic), "KafkaPublisher")

  system.awaitTermination
}


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
    //.setJars(Seq("/path/jar"))

  val ssc = new SparkContext(conf)
  val wordCount = system.actorOf(WordCountActor.props(ssc), "word-count")

  val future = wordCount ? GetTopKWordCount("RT", 10)
  future onSuccess {
    case TopKWordCount(word, top) => println(top)
  }
}






































