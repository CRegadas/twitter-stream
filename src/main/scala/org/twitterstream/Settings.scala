package org.twitterstream

import java.util.Properties
import com.typesafe.config.ConfigFactory
import kafka.javaapi.producer.Producer
import kafka.producer.ProducerConfig

final object Settings extends Serializable {
  private val rootConfig = ConfigFactory.load().getConfig("twitterstream")

  object Twitter {
    val config = rootConfig.getConfig("twitter")

    val appKey: String = config.getString("key")
    val appSecret: String = config.getString("secret")
    val accessToken: String = config.getString("accessToken")
    val accessTokenSecret: String = config.getString("accessTokenSecret")
  }

  object Kafka {
    val config = rootConfig.getConfig("kafka")

    val brokers: String = config.getString("metadata.broker.list")
    val serializer: String = config.getString("serializer.class")
    val producerType: String = config.getString("producer.type")
    val compressionCodec: String = config.getString("compression.codec")

    val tweetsTopic: String = config.getString("topic.tweets")

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

  object Cassandra {
    val config = rootConfig.getConfig("cassandra")

    val keySpace: String = config.getString("keyspace")
    val tableRaw: String = config.getString("table.raw")
    val tableWordCount: String = config.getString("table.wordcount")
  }
}
