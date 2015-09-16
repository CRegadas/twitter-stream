package controllers

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.{SparkConf, SparkContext}
import org.twitterstream.Settings.{Spark => Config}
import org.twitterstream.analytics._
import org.twitterstream.core._
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

class Application extends Controller {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("ts-client")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  implicit val timeout = Timeout(5.seconds)

  def index = Action.async {
    Future {
      Ok(views.html.index.render())
    }
  }

  // endpoint that opens an echo websocket
  def wsEcho = WebSocket.using[String] {
    request => {

      //unicast
     /* var channel: Option[Concurrent.Channel[String]] = None
      val out: Enumerator[String] = Concurrent.unicast(c => channel = Some(c))*/

      // broadcast
       val (out, channel) = Concurrent.broadcast[String]

      // init spark
      val sparkContext = setSparkConf()

      val in = Iteratee.foreach[String]
      {
        _ match
        {
          case "INIT" => println("INIT"); channel.push("INIT")
          case "TOP" => wordCount(channel, sparkContext)
          case "CLOSE" => channel.eofAndEnd()
        }
      }

      (in, out)
    }
  }

  def setSparkConf() = {

    val conf = new SparkConf()
      .setMaster(Config.master)
      .set("spark.cassandra.connection.host", Config.cassandraHost)
      .set("spark.cores.max", "4")
      .setAppName("ts-client-app")
      .setJars(Seq(Config.jars))

    new SparkContext(conf)

  }

  def wordCount(channel: Channel[String], ssc: SparkContext) = {

    val wordCount = system.actorOf(HashtagCountActor.props(ssc), "hashtag-count")

    println("ENTREI WORDCOUNT")

    implicit val writer = new Writes[Seq[(String, Int)]] {
      def writes(t: Seq[(String, Int)]): List[JsValue] = {
        var jsValueList = List[JsValue]()
        t.foreach(pair => Json.obj("label" -> pair._1 + ",value" -> pair._2) :: jsValueList)
        jsValueList
      }
    }

    val future = wordCount ? GetTopKHashtagCount(5)
    future onSuccess {
      case TopKHashtagCount(top) => {
          Try(channel.push(Ok(Json.toJson(top)).toString()) match {
            case Failure(m) => channel.end()
            case m => {
              println(m)
              println("RESULTADO de TopKHashtagCount")
              println(top)
            }
          })
      }
    }
  }


}