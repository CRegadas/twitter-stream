package org.twitterstream.analytics

import akka.actor._
import akka.pattern.pipe
import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._
import org.apache.spark.SparkContext
import org.apache.spark.streaming.dstream.DStream
import org.twitterstream.Settings.{Cassandra => Config}
import org.twitterstream.core._

case class HashtagCountStream(stream: DStream[String])  {
  val words = stream.flatMap(_.split(" ")).filter(_.startsWith("#"))
  /** Saves the raw data to Cassandra - raw table. */
  //stream.saveToCassandra(Config.keySpace, Config.tableRaw)

  words.map(word => (word, 1)).saveToCassandra(Config.keySpace, Config.tableHashtagCount)

  words.print

}


object HashtagCountActor {
  def props(sc: SparkContext) = Props(classOf[HashtagCountActor], sc)
}

class HashtagCountActor(sc: SparkContext) extends Actor with ActorLogging {
  implicit val ctx = context.dispatcher

  override def receive: Receive = {
    case GetTopKHashtagCount(k) => topK(k, sender)
  }

  def topK(k: Int, requester: ActorRef): Unit = {

    println("ENTREI TOPK")

    val toTopK = (aggregate: Seq[(String, Int)]) => TopKHashtagCount(
      sc.parallelize(aggregate).collect().toList.sortBy(_._2).reverse.take(k))

    sc.cassandraTable[(String, Int)](Config.keySpace, Config.tableHashtagCount).cache()
      .collectAsync().map(toTopK) pipeTo requester

    /** Saves the top hashtag to Cassandra, top_hashtag table **/
/*    val ord = sc.cassandraTable[(String, Int)](Config.keySpace, Config.tableHashtagCount).cache()
      .collect().toList.sortBy(_._2).reverse.take(k)

    val cena = sc.parallelize(ord)
    cena.saveToCassandra(Config.keySpace, Config.tableTopHashtag)*/

  }
}
