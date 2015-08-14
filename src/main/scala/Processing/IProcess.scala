package Processing

import org.apache.spark.streaming.dstream.DStream
import twitter4j.HashtagEntity

trait IProcess[T] {

  def start() : scala.Unit = { }
  def getHashtagsList() : DStream[(HashtagEntity, String)]
  def collect() : T
  def setStreamingLogLevels() : scala.Unit = { }

}
