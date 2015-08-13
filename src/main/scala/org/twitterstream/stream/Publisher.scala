
package org.twitterstream.stream

import akka.actor._
import org.twitterstream.utils._

trait PublisherActor extends Actor with ActorLogging {

  def publish(tweet: Tweet) = context.system.eventStream.publish(tweet)

  override def receive: Receive = {
    case _ =>
  }

}
