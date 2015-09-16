package org.twitterstream.stream

import twitter4j._
import org.twitterstream.core.{Tweet, Author}
import org.twitterstream.utils.TwitterStreamClient

class TwitterStream extends PublisherActor {

  def simpleStatusListener = new StatusListener() {
    def onStatus(s: Status) {
      val tweet = Tweet(Author(s.getUser.getScreenName), s.getCreatedAt.getTime, s.getText)
      publish(tweet)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) {
      ex.printStackTrace
    }

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  override def preStart(): Unit = {
    val stream = new TwitterStreamClient(simpleStatusListener)
    stream.init
  }


}
