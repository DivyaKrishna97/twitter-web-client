package controllers

import play.api._
import play.api.mvc._
import securesocial.core.SecureSocial
import traits._

object Client extends Controller
    with SecureSocial
    with AuthenticatedUser {

  def home = SecuredAction { implicit request =>
    Ok(views.html.Client.home(user))
  }

}

