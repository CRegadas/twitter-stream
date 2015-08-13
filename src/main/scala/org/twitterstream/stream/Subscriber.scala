package org.twitterstream.stream

import akka.actor._
import org.twitterstream.utils._

trait SubscriberActor extends Actor with ActorLogging {

  def subscribe = context.system.eventStream.subscribe(self, classOf[Tweet])

  def unsubscribe = context.system.eventStream.unsubscribe(self)

  override def preStart(): Unit = subscribe

  override def postStop(): Unit = unsubscribe

}

object SubscriberFunc {
  def handle(fn: Tweet => Unit, name: String)(implicit system: ActorSystem): ActorRef = {
      val props = Props(classOf[SubscriberFunc], fn)
      system.actorOf(props, name)
  }
}


class SubscriberFunc(fn: Tweet => Unit) extends SubscriberActor {
  override def receive: Receive = {
    case tweet: Tweet => 
      log.info("received: {}", tweet)
      fn(tweet)
  }
}
