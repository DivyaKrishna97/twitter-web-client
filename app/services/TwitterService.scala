package services

import scala.collection.JavaConverters._
import scala.util.Try
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

/** Wraps the Twitter4J library to be more Scala compatible */
trait TwitterService {
  /** Returns tweets in the user's home timeline */
  def homeTimeline: Try[List[Status]]

  /** Returns tweets in the user's home timeline before the tweet with the given ID.
   *
   * @param beforeTweetId
   */
  def homeTimeline(beforeTweetId: Long): Try[List[Status]]
}

object TwitterService {

  val consumerKey = sys.env("TWITTER_CONSUMER_KEY")
  val consumerSecret = sys.env("TWITTER_CONSUMER_SECRET")

  /** Construct a TwitterService
   *
   * @param token User's access token retrieved from OAuth
   * @param secret User's access token secret retrieved from OAuth
   */
  def apply(token: String, secret: String) = new TwitterServiceImpl(client(token, secret))

  private def client(token: String, secret: String): Twitter = {
    val config = new ConfigurationBuilder()
      .setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecret)
      .setOAuthAccessToken(token)
      .setOAuthAccessTokenSecret(secret)
      .build
    new TwitterFactory(config).getInstance
  }

}

class TwitterServiceImpl(val client: Twitter) extends TwitterService {
  def homeTimeline = Try { client.getHomeTimeline.asScala.toList }

  def homeTimeline(beforeTweetId: Long) = {
    Try {
      val paging = new Paging
      paging.setMaxId(beforeTweetId - 1)
      client.getHomeTimeline(paging).asScala.toList
    }
  }
}

