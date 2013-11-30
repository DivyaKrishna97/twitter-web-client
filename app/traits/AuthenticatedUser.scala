package traits

import play.api.mvc._
import securesocial.core._

trait AuthenticatedUser {

  implicit def user(implicit request: RequestHeader): Option[Identity] = {
    SecureSocial.currentUser
  }

}
