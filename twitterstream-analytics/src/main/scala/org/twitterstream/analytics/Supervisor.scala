package org.twitterstream.analytics

import akka.actor._
import org.apache.spark.streaming._

import org.twitterstream.core._
import org.twitterstream.Settings._

object Supervisor {
  def props(ssc: StreamingContext) =
    Props(classOf[Supervisor], ssc)
}

class Supervisor(ssc: StreamingContext) extends Actor with ActorLogging {

  override def preStart: Unit = {
    context.actorOf(KafkaStreaming.props(ssc, Set(Kafka.tweetsTopic)), "kafka-stream")

    context become initialize
  }

  def initialize: Receive = {
    case StreamReady =>
      ssc.start
      context become receive
  }

  override def receive: Receive = {
    case _ => //ignore
  }

  override def postStop: Unit = ssc.awaitTermination
}
