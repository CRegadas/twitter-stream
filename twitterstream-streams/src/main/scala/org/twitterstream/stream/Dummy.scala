package org.twitterstream.stream

import akka.actor._
import org.twitterstream.core.Tweet

object Dummy {
  def nothing(tweet: Tweet): Unit = {}
}
