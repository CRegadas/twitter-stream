package Processing

import scala.collection.immutable.HashMap

import Services.IServices
import org.apache.spark.streaming.dstream.DStream
import twitter4j._
import collection.JavaConversions._
import scala.util.matching.Regex


class FilterControl[T](process: IProcess[DStream[(T, String)]], service: IServices[T], twitter : Twitter) {

  /** Collect hashtags and store in redis **/
  def filterByHashTags =
  {

  	println("------------------------------------------FILTER_CONTROL")

    //var t = Array[(T, String)]()
    val data: DStream[(T, String)] = process.collect()
    println("HASHTAGS no FILTER_CONTROL recolhidas do Kafka: "+data.print())

	  data.foreachRDD( rdd => service.writeHashtags(rdd.collect()) )

    process.start()

  }

  def findTweetsByTags(tag : String) : Array[Status] =
  {
    // #ShikakaMusicBox
    val query = new Query(tag)
    // Max allowed is 100
    query.setCount(50)
    query.getSince
    val qr: QueryResult = twitter.search(query)
    val tweetsByHashtag = (qr.getTweets: Iterable[Status]).toArray
    tweetsByHashtag.foreach(status =>{
      println("---------------------------------- USER "+status.getUser.getScreenName+" subscreveu o servico ShikakaMusicBox!")
      service.writePlaylist(status.getText);

    })

    tweetsByHashtag
  }

  def topHashtags =
  {
    //var max : Long = 0

    val tag: Regex = """HashTag: *""".r
    val res: HashMap[String, Long] = service.count(tag)
    println("-----------------------------------Teste tophashtags: "+res)
    res.foreach(println)

    //val res = client.keys(tag)
    //res.foreach(println)

  }

  def teste(tag : String) =
  {
    println("--------------------------------------FILTERCONTROL_TESTE")
    service.readTweetsByTag(tag)
  }




}
