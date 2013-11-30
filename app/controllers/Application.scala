package controllers

import play.api._
import play.api.mvc._
import securesocial.core.SecureSocial
import traits._

object Application extends Controller
    with SecureSocial
    with AuthenticatedUser {

  def index = UserAwareAction { implicit request =>
    request.user match {
      case Some(user) => Redirect(routes.Client.home)
      case _ => Ok(views.html.index(user))
    }
  }

}
