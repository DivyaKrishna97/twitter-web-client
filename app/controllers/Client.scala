package controllers

import scala.util.{Success, Failure}

import play.api._
import play.api.mvc._
import play.api.libs.json._
import securesocial.core.SecureSocial

import services.TwitterService
import models._
import traits._


object Client extends Controller
    with SecureSocial
    with AuthenticatedUser {

  def home = SecuredAction { implicit request =>
    Ok(views.html.Client.home(jsonTimeline, TweetData.createForm))
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
      case None => BadRequest("beforeId parameter required")
    }
  }

  def createStatus = SecuredAction(parse.multipartFormData) { implicit request =>
    // TODO CSRF protection
    TweetData.createForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.Client.home(jsonTimeline, formWithErrors))
      },
      tweetData => {
        val (token, secret) = userAccessContext.get
        val photo = for { photo <- request.body.file("photo") } yield photo.ref.file
        TwitterService(token, secret).createStatusUpdate(tweetData, photo)
        // TODO handle success and failure condition
        Redirect(routes.Client.home)
      }
    )
  }

  private def jsonTimeline(implicit request: RequestHeader) = {
    val (token, secret) = userAccessContext.get
    val timeline = TwitterService(token, secret).homeTimeline match {
      case Success(timeline) => timeline.map(StatusExt(_).toJson)
      case Failure(e) => Seq.empty
    }
    Json.stringify(Json.toJson(timeline))
  }

}

