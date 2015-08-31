package org.twitterstream.store

import org.twitterstream.core.Tweet

trait Store {
  def save(tweet: Tweet): Unit
}
