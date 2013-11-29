package controllers

import play.api._
import play.api.mvc._
import securesocial.core.SecureSocial

object Application extends Controller with SecureSocial {

  def index = UserAwareAction { implicit request =>
    request.user match {
      case Some(user) => Redirect(routes.Protected.index)
      case _ => Ok(views.html.index())
    }
  }

}
