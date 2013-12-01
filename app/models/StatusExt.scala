package models

import play.api.libs.json._
import twitter4j._


class StatusExt(val status: Status) {

  def toJson = {
    val user = status.getUser
    Json.toJson(
      Map(
        "id" -> Json.toJson(status.getId.toString),
        "message" -> Json.toJson(status.getText),
        "author" -> Json.toJson(user.getName),
        "screenName" -> Json.toJson(user.getScreenName),
        "avatarUrl" -> Json.toJson(user.getProfileImageURL)
      )
    )
  }

}

object StatusExt {

  def apply(status: Status) = new StatusExt(status)

}
