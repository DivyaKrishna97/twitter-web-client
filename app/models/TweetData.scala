package models

import play.api.data._
import play.api.data.Forms._

case class TweetData(status: String,
                     geolocation: Boolean,
                     longitude: BigDecimal,
                     latitude: BigDecimal)

object TweetData {
  def createForm = {
    Form(
      mapping(
        "status" -> nonEmptyText(minLength = 1, maxLength = 140),
        "geolocation" -> boolean,
        "longitude" -> bigDecimal,
        "latitude" -> bigDecimal
      )(TweetData.apply)(TweetData.unapply)
    )
  }
}

