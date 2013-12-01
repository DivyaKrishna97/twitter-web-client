package services

import scala.collection.JavaConverters._
import scala.util.Try
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import models.TweetData
import play.api.mvc.MultipartFormData.FilePart
import java.io.File

/** Wraps the Twitter4J library to be more Scala compatible */
trait TwitterService {
  /** Returns tweets in the user's home timeline */
  def homeTimeline: Try[List[Status]]

  /** Returns tweets in the user's home timeline before the tweet with the given ID.
   *
   * @param tweetId
    *@return the list of tweets/statuses, if successful
   */
  def homeTimelineBefore(tweetId: Long): Try[List[Status]]

  /** Returns tweets in the user's home timeline after the tweet with the given ID.
   *
   * @param tweetId
   * @return
   */
  def homeTimelineAfter(tweetId: Long): Try[List[Status]]

  /** Create a new status update tweet.
   *
   * @param data Validated form data used to create the new tweet
   * @param photo Optional file attached for the tweet
    *@return the created status, if successful
   */
  def createStatusUpdate(data: TweetData, photo: Option[File]): Try[Status]
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

  def homeTimelineBefore(tweetId: Long) = {
    Try {
      val paging = new Paging
      paging.setMaxId(tweetId - 1)
      client.getHomeTimeline(paging).asScala.toList
    }
  }

  def homeTimelineAfter(tweetId: Long) = {
    Try {
      val paging = new Paging
      // Twitter API doesn't seem to like 0 as a sentinel value
      if (tweetId > 0) paging.setSinceId(tweetId)
      client.getHomeTimeline(paging).asScala.toList
    }
  }

  def createStatusUpdate(data: TweetData, photo: Option[File]) = {
    Try {
      val status = new StatusUpdate(data.status)
      if (data.geolocation) {
        status.setLocation(new GeoLocation(data.latitude.toDouble, data.longitude.toDouble))
        status.setDisplayCoordinates(true)
      }
      for { file <- photo } status.setMedia(file)
      client.updateStatus(status)
    }
  }
}

