package controllers

import play.api._
import play.api.mvc._
import securesocial.core.SecureSocial

object Protected extends Controller with SecureSocial {

  def index = SecuredAction { implicit request =>
    val user = request.user
    val (token, secret) = user.oAuth1Info match {
      case Some(info) => (info.token, info.secret)
      case None => (null, null)
    }
    Ok(s"Name: ${user.fullName} Token: $token Secret: $secret")
  }

}

