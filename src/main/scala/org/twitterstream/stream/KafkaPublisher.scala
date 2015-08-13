package org.twitterstream.stream

import com.typesafe.config.ConfigFactory
import java.util.Properties
import kafka.javaapi.producer.Producer
import kafka.producer.{ProducerConfig, KeyedMessage}

import org.twitterstream.utils._

object KafkaConfig {
  val config = ConfigFactory.load()
  val brokers: String = config.getString("kafka.metadata.broker.list")
  val serializer: String = config.getString("kafka.serializer.class")
  val producerType: String = config.getString("kafka.producer.type")
  val compressionCodec: String = config.getString("kafka.compression.codec")

  def properties: Properties = {
    val props = new Properties
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", serializer)
    props.put("producer.type", producerType)
    props.put("compression.codec", compressionCodec)

    props
  }

  def producerConfig: ProducerConfig = new ProducerConfig(properties)
}

class KafkaPublisher(val topic: String) extends SubscriberActor {

  private var producer: Producer[String, Array[Byte]] = null

  override def preStart: Unit = {
    val config = KafkaConfig.producerConfig
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
