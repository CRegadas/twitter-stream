package org.twitterstream.core

case class TopKWordCount(word: String, top: Seq[Int])

final case class Author(handle: String)

final case class Hashtag(name: String)

case class Tweet(author: Author, timestamp: Long, body: String) {
  def hashtags: Set[Hashtag] =
    body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t)}.toSet
}

final object EmptyTweet extends Tweet(Author(""), 0L, "")
