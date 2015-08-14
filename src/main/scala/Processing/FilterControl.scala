package Processing

import Services.IServices
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import twitter4j._

import scala.collection.JavaConversions._


class FilterControl[T](process: IProcess[DStream[(T, String)]], service: IServices[T], twitter : Twitter) {

  /** Collect hashtags and store in redis **/
  def filterByHashTags =
  {
  	println("------------------------------------------FILTER_CONTROL")

    //var t = Array[(T, String)]()
    val data: DStream[(T, String)] = process.collect()
    println("HASHTAGS no FILTER_CONTROL recolhidas do Kafka: "+data.print())

	  data.foreachRDD( rdd => service.writeHashtags(rdd.collect()) )

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

  // consultar pelo redis
/*  def topHashtags(n : Int) =
  {
    val tag: Regex = """HashTag: *""".r
    val res: List[(String, Long)] = service.count(tag)
    val resOrderByValue: List[(String, Long)] = res.sortWith((x,y) => x._2 < y._2)
    resOrderByValue.foreach(println)
    val res2 = resOrderByValue.takeRight(n).sortWith((x,y) => x._2 > y._2)
    println("-------------------- TOP5 ")
    res2.foreach(println)

  }*/

  def topHashtagsLive(seg : Int) =
  {
    val data: DStream[(HashtagEntity, String)] = process.getHashtagsList()

    val topCountsN: DStream[(Int, (HashtagEntity, String))] = data.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(seg))
                                                                  .map{case (topic, count) => (count, topic)}
                                                                  .transform(_.sortByKey(false))
    topCountsN.foreachRDD( rdd => {


      var list = List[(String, Int)]()
      rdd.collect().foreach(pair => list = (pair._2._1.getText, pair._1) :: list)
      val orderList : List[(String, Int)] = list.sortWith((x,y) => x._2 > y._2)
      println("\nPopular topics in last N seconds (%s total):".format(rdd.count()))
      orderList.take(5).foreach(println)

    })


  }

  def teste(tag : String) =
  {
    println("--------------------------------------FILTERCONTROL_TESTE")
    service.readTweetsByTag(tag)
  }

  def toDo() =
  {
    filterByHashTags
    topHashtagsLive(10)
    process.start()

  }



}
