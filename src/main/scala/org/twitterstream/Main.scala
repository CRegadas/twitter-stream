package org.twitterstream

import akka.actor._
import scala.concurrent._
import java.util.concurrent.{ExecutorService, Executors}

import org.twitterstream.store._
import org.twitterstream.stream._


import Processing.{FilterControl, Spark}
import Services.{KafkaService, Redis}

object Main extends App {
        // ActorSystem & thread pools
        val execService: ExecutorService = Executors.newCachedThreadPool()
        implicit val system: ActorSystem = ActorSystem("ts")
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)
        //implicit val materializer = ActorFlowMaterializer()(system)


        /** ask to filter data **/
        /*println("------------------------------------------MAIN_filter control")*/
        //val tFactory: Twitter = new TwitterFactory(config).getInstance()
        //val processor = new Spark
        //val filter = new FilterControl[HashtagEntity](processor, store, tFactory)
        //filter.filterByHashTags
        /*filter.topHashtags*/

        //filter.filterByHashTags()
        //filter.teste("ShikakaMusicBox")
        /*filter.findTweetsByTags("#ShikakaMusicBox").foreach(tweet =>
                println("-------------------------------------- TWEET BY TAG:  "+tweet.getText))*/

        val twitterStream = system.actorOf(Props[TwitterStream], "twitterstream")

        SubscriberFunc.handle(Dummy.nothing, "dummy")
        SubscriberFunc.handle(RedisStore.save, "timeline-store")

        val kafkaPublisherProps = Props(classOf[KafkaPublisher], "test")
        val kafkaPublisher = system.actorOf(kafkaPublisherProps, "kafkaPublisher")


}







































