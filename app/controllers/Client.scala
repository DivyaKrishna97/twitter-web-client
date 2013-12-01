package controllers

import scala.util.{Try, Success, Failure}

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.filters.csrf._
import securesocial.core.SecureSocial

import services.TwitterService
import models._
import traits._


object Client extends Controller
    with SecureSocial
    with AuthenticatedUser {

  def home = CSRFAddToken {
    SecuredAction { implicit request =>
      Ok(views.html.Client.home(jsonTimeline, TweetData.createForm))
    }
  }

  // Returns tweets in the timeline before the ID
  def historical = SecuredAction(ajaxCall = true) { implicit request =>
    request.getQueryString("beforeId") match {
      case Some(beforeId) => {
        val (token, secret) = userAccessContext.get

        val timeline = for {
            id <- Try { beforeId.toLong }
            result <- TwitterService(token, secret).homeTimeline(id)
        } yield result

        timeline match {
          case Success(timeline) => Ok(Json.toJson(timeline.map(StatusExt(_).toJson)))
          case Failure(_) => InternalServerError("Cannot retrieve timeline")
        }
      }
      case None => BadRequest("beforeId parameter required")
    }
  }

  def createStatus = CSRFCheck {
    SecuredAction(parse.multipartFormData) { implicit request =>
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

