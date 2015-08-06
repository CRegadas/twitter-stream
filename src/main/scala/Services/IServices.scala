package Services

import scala.collection.immutable.HashMap

import twitter4j.Status

import scala.util.matching.Regex

trait IServices[T] {

  def writeHashtags(hashs: Array[(T, String)]) : scala.Unit = { }
  def writeStatus(status : Status) : scala.Unit = { }
  def writePlaylist(tweet: String) : scala.Unit = { }
  def readTweetsByTag(tag: String) : scala.Unit = { }
  def read() : scala.Unit = { }
  def count(tag: Regex) : HashMap[String, Long]

}
