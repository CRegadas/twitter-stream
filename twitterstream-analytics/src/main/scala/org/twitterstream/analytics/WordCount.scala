package org.twitterstream.analytics

import scala.reflect.ClassTag

import akka.actor._
import akka.pattern.pipe
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

import com.datastax.spark.connector.streaming._
import org.apache.spark.SparkContext
import org.apache.spark.util.StatCounter
import org.apache.spark.SparkContext._
import com.datastax.spark.connector._

import org.twitterstream.Settings.{Cassandra => Config}
import org.twitterstream.core._

case class WordCountStream(stream: DStream[String])  {
  val words = stream.flatMap(_.split(" ")).filter(_.nonEmpty)
  /** Saves the raw data to Cassandra - raw table. */
  //stream.saveToCassandra(Config.keySpace, Config.tableRaw)
  
  words.map(word => (word, 1)).saveToCassandra(Config.keySpace, Config.tableWordCount)

  words.print
}


object WordCountActor {
  def props(sc: SparkContext) = Props(classOf[WordCountActor], sc)
}

class WordCountActor(sc: SparkContext) extends Actor with ActorLogging {
  implicit val ctx = context.dispatcher

  override def receive: Receive = {
    case GetTopKWordCount(word, k) => topK(word, k, sender)
  }

  def topK(word: String, k: Int, requester: ActorRef): Unit = {
    val toTopK = (aggregate: Seq[Int]) => TopKWordCount(word,
      sc.parallelize(aggregate).top(k).toSeq)

    sc.cassandraTable[Int](Config.keySpace, Config.tableWordCount)
      .select("count")
      .where("word = ?", word)
      .collectAsync().map(toTopK) pipeTo requester
  }
}
