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

    val timeline = TwitterService(token, secret).homeTimeline match {
      case Success(timeline) => timeline.map(StatusExt(_).toJson)
      case Failure(e) => Seq.empty
    }
    val jsonTimeline = Json.stringify(Json.toJson(timeline))
    Ok(views.html.Client.home(jsonTimeline))
  }

  // Returns tweets in the timeline before the ID
  def historical = SecuredAction(ajaxCall = true) { implicit request =>
    request.getQueryString("beforeId") match {
      case Some(beforeId) => {
        val (token, secret) = userAccessContext.get
        // TODO handle the case where beforeId is not an integer
        TwitterService(token, secret).homeTimeline(beforeId.toLong) match {
          case Success(timeline) => Ok(Json.toJson(timeline.map(StatusExt(_).toJson)))
          case Failure(e) => InternalServerError(e.getMessage)
        }
      }
      case None => Status(422)("beforeId parameter required")
    }
  }

}

