package services

import scala.collection.JavaConverters._
import scala.util.Try
import twitter4j._
import twitter4j.conf.ConfigurationBuilder


object TwitterService {

  val consumerKey = sys.env("TWITTER_CONSUMER_KEY")
  val consumerSecret = sys.env("TWITTER_CONSUMER_SECRET")

  def homeTimeline(token: String, secret: String): Try[List[Status]] = {
    Try { client(token, secret).getHomeTimeline.asScala.toList }
  }

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

