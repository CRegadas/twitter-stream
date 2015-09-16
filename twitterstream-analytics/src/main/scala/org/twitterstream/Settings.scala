package org.twitterstream

import java.util.Properties

import com.typesafe.config.ConfigFactory
import kafka.producer.ProducerConfig

final object Settings extends Serializable {
  private val rootConfig = ConfigFactory.load().getConfig("twitterstream")

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
    val tableHashtagCount: String = config.getString("table.hashtagcount")
    val tableTopHashtag: String = config.getString("table.tophashtag")
  }

  object Spark {
    val config = rootConfig.getConfig("spark")

    val master: String = config.getString("master")
    val cassandraHost: String = config.getString("spark.cassandra.connection.host")
    val jars: String = config.getString("jars")
  }
}
