package org.twitterstream

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor._
import org.apache.spark._
import org.apache.spark.streaming._
import org.twitterstream.Settings.{Spark => Config}
import org.twitterstream.analytics._

import scala.concurrent._


object AnalyticsApp extends App {
  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-analytics")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  val conf = new SparkConf()
    .setMaster(Config.master)
    .set("spark.cassandra.connection.host", Config.cassandraHost)
    .set("spark.cores.max", "4")
    .setAppName("ts-analytics")


  val ssc = new StreamingContext(conf, Milliseconds(400))
  system.actorOf(Supervisor.props(ssc), "supervisor")

  system.awaitTermination
}





























