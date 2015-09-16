package org.twitterstream.core

case object StreamReady

sealed trait Request

sealed trait HashtagCountRequest extends Request
case class GetTopKHashtagCount(k: Int) extends HashtagCountRequest
