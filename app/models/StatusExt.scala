package models

import play.api.libs.json._
import twitter4j._


class StatusExt(val status: Status) {

  def toJson = {
    val message = if (status.isRetweet) status.getRetweetedStatus else status
    val user = message.getUser

    Json.toJson(
      Map(
        "id" -> Json.toJson(message.getId.toString),
        "message" -> Json.toJson(message.getText),
        "author" -> Json.toJson(user.getName),
        "screenName" -> Json.toJson(user.getScreenName),
        "avatarUrl" -> Json.toJson(user.getProfileImageURL),
        "retweet" -> Json.toJson(status.isRetweet),
        "retweetedBy" -> Json.toJson(if (status.isRetweet) status.getUser.getName else null)
      )
    )
  }

}

object StatusExt {

  def apply(status: Status) = new StatusExt(status)

}
