package org.twitterstream.utils

import twitter4j._
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder

object TwitterClient {
  import org.twitterstream.Settings.{Twitter => Config}

  def apply(): Twitter = {
    val factory = new TwitterFactory(new ConfigurationBuilder().build())
    val t = factory.getInstance()
    t.setOAuthConsumer(Config.appKey, Config.appSecret)
    t.setOAuthAccessToken(new AccessToken(Config.accessToken, Config.accessTokenSecret))
    t
  }
}

class TwitterStreamClient(listener: StatusListener) {
  import org.twitterstream.Settings.{Twitter => Config}

  val factory = new TwitterStreamFactory(new ConfigurationBuilder().build())
  val twitterStream = factory.getInstance()

  def init = {
    twitterStream.setOAuthConsumer(Config.appKey, Config.appSecret)
    twitterStream.setOAuthAccessToken(new AccessToken(Config.accessToken, Config.accessTokenSecret))
    twitterStream.addListener(listener)
    twitterStream.sample
  }

  def stop = {
    twitterStream.cleanUp
    twitterStream.shutdown
  }

}

