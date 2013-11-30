package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.util.{Try, Success, Failure}
import securesocial.core.SecureSocial
import services.TwitterService
import models.StatusExt
import traits._


object Client extends Controller
    with SecureSocial
    with AuthenticatedUser {

  def home = SecuredAction { implicit request =>
    val (token, secret) = userAccessContext.get

    val timeline = TwitterService.homeTimeline(token, secret) match {
      case Success(timeline) => timeline.map(StatusExt(_).toJson)
      case Failure(e) => Seq.empty
    }
    val jsonTimeline = Json.stringify(Json.toJson(timeline))
    Ok(views.html.Client.home(jsonTimeline))
  }

}

