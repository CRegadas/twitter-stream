package org.twitterstream.core

case object StreamReady

sealed trait Request

sealed trait WordCountRequest extends Request
case class GetTopKWordCount(word: String, k: Int) extends WordCountRequest
