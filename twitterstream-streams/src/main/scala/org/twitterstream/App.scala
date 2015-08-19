package org.twitterstream

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.{ExecutorService, Executors}

import org.twitterstream.Settings._
import org.twitterstream.stream._

import org.apache.spark._
import org.apache.spark.streaming._


object StreamApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-stream")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  system.actorOf(Props[TwitterStream], "twitterstream")
  system.actorOf(KafkaPublisher.props(Kafka.tweetsTopic), "KafkaPublisher")

  system.awaitTermination
}
