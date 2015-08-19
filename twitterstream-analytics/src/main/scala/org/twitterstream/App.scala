package org.twitterstream

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.{ExecutorService, Executors}

import org.twitterstream.analytics._

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





























