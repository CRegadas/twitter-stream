package org.twitterstream.analytics

import scala.collection.JavaConverters._

import akka.actor._
import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

import org.twitterstream.core._
import org.twitterstream.Settings._

object KafkaStreaming {
  def props(ssc: StreamingContext, topics: Set[String]) = 
    Props(classOf[KafkaStreaming], ssc, topics)
}

class KafkaStreaming(ssc: StreamingContext, topics: Set[String]) extends Actor with ActorLogging {
  private val kafkaParams = Kafka.properties.asScala.toMap
  private val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
    ssc,
    kafkaParams,
    topics
  ).map(_._2)

  HashtagCountStream(stream)

  override def preStart: Unit = 
    context.parent ! StreamReady

  override def receive: Receive = {
    case _ => //ignore
  }
}
