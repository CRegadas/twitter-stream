package Main

import java.util.Properties

import Collect.TwitterStream
import Processing.{FilterControl, Spark}
import Services.{KafkaService, Redis}
import kafka.javaapi.producer.Producer
import kafka.producer.ProducerConfig
import org.apache.spark.Logging
import twitter4j._
import twitter4j.conf.ConfigurationBuilder


object Main extends App with Logging
{
        val topic = "teste"

        /** TwitterStream configuration **/
        val config = new ConfigurationBuilder()
        .setOAuthConsumerKey("BBC5CelxP2NKtSjzGrjKfEHbl")
        .setOAuthConsumerSecret("Y5k7hCkHDTTSJzUwWXBz4O30bHU6rlGydAZTedEc8GHVTvBiyG")
        .setOAuthAccessToken("3166270558-x1uMGZ2t9gcqOEQr90ww6OKDMGKI0n7xYdIKUJz")
        .setOAuthAccessTokenSecret("wWJXdqAgyR5iAXTgjqrMxsHvLoBWjVZCgTMbn5txEJQi8")
        .setJSONStoreEnabled(true)
        .build

        val stream: twitter4j.TwitterStream = new TwitterStreamFactory(config).getInstance()
        //val stream = new MockTwitterStream

        /** Zookeeper connection properties **/
        val props = new Properties
        props.put("metadata.broker.list", "localhost:9092")
        props.put("serializer.class", "kafka.serializer.DefaultEncoder")
        props.put("producer.type", "sync")
        props.put("compression.codec", "gzip")

        val pconfig = new ProducerConfig(props)
        val producer = new Producer[String, Array[Byte]](pconfig)

        /** get data from twitter stream **/
        println("------------------------------------------MAIN_service kafka")
        val serviceK = new KafkaService(producer,topic)
        val serviceR = new Redis
        new TwitterStream(stream,serviceK,serviceR)

        /** ask to filter data **/
        println("------------------------------------------MAIN_filter control")
        val tFactory: Twitter = new TwitterFactory(config).getInstance()


        val processH = new Spark
        val filter = new FilterControl[HashtagEntity](processH, serviceR, tFactory)
        filter.toDo()
        /*filter.filterByHashTags
        filter.topHashtags(5)*/

        //filter.filterByHashTags()
        //filter.teste("ShikakaMusicBox")
        /*filter.findTweetsByTags("#ShikakaMusicBox").foreach(tweet =>
                println("-------------------------------------- TWEET BY TAG:  "+tweet.getText))*/

        Thread.sleep(10000)


}







































