package org.twitterstream.store

import com.redis.RedisClient
import org.twitterstream.core.Tweet

object RedisStore extends Store {

  val client = new RedisClient("localhost", 6379)

  def save(tweet: Tweet): Unit = {
    client.pipeline { p =>
      p.lpush("ts:timeline", tweet)
      p.lpush(s"ts:users:${tweet.author.handle}", tweet)
    }
  }
  
}
