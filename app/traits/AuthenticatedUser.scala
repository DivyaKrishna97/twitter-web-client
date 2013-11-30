package traits

import play.api.mvc._
import securesocial.core._

trait AuthenticatedUser {

  implicit def user(implicit request: RequestHeader): Option[Identity] = {
    SecureSocial.currentUser
  }

  def userAccessContext(implicit request: RequestHeader): Option[(String, String)] = {
    for {
      user <- SecureSocial.currentUser
      oauthInfo <- user.oAuth1Info
    } yield (oauthInfo.token, oauthInfo.secret)
  }

}
