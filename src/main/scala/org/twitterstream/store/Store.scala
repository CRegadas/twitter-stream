package org.twitterstream.store

import org.twitterstream.utils._

trait Store {
  def save(tweet: Tweet): Unit
}
