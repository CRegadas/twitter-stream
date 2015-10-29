package controllers

import java.util.concurrent.{ExecutorService, Executors}
import javax.inject._

import akka.actor.{ActorRef, ActorSystem}
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
import scala.util.{Try, Failure}

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)

  val sparkContext = setSparkConf()
  val actorRef = system.actorOf(HashtagCountActor.props(sparkContext), "hashtag-count")

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

      val in = Iteratee.foreach[String]
      {
        _ match
        {
          case "INIT" => println("INIT"); channel.push("INIT")
          case "TOP" => hashtagCount(channel, actorRef)
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


  implicit val writer = new Writes[Seq[(String, Int)]] {
    def writes(t: Seq[(String, Int)]): JsArray = {

      val x = t.map(pair => {
        JsObject(Seq(
          "label" -> JsString(pair._1),
          "value" -> JsNumber(pair._2)
        ))
      })
      new JsArray(x)
    }
  }


  def hashtagCount(channel: Channel[String], actorRef: ActorRef) = {

    println("ENTREI WORDCOUNT")

    val future = actorRef ? GetTopKHashtagCount(5)
    future onSuccess {
      case TopKHashtagCount(top) => {
          Try(channel.push(Json.stringify(Json.toJson(top)))) match {
            case Failure(f) => channel.end()
            case m => println(m)
          }
      }
    }


  }



}
