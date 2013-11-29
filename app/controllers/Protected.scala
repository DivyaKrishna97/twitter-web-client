package controllers

import play.api._
import play.api.mvc._
import securesocial.core.SecureSocial

object Protected extends Controller with SecureSocial {

  def index = SecuredAction { implicit request =>
    Ok(request.user.fullName)
  }

}

