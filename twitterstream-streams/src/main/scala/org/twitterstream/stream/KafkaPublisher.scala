package org.twitterstream.stream

import akka.actor._
import kafka.javaapi.producer.Producer
import kafka.producer.KeyedMessage

import org.twitterstream.core.Tweet

object KafkaPublisher {
  def props(topic: String) = Props(classOf[KafkaPublisher], topic)
}

class KafkaPublisher(val topic: String) extends SubscriberActor {
  import org.twitterstream.Settings.Kafka

  private var producer: Producer[String, Array[Byte]] = null
  private lazy val config = Kafka.producerConfig

  override def preStart: Unit = {
    producer = new Producer[String, Array[Byte]](config)
    subscribe
  }

  override def receive: Receive = {
    case tweet: Tweet =>
      val msg = tweet.body.getBytes("UTF8")

      producer.send(new KeyedMessage[String, Array[Byte]](topic, msg))
      log.info("Kafka topic: {} msg: {}", topic, msg)
  }

}
