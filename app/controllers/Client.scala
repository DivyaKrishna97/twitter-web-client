package controllers

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.filters.csrf._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
  def statusBefore = SecuredAction(ajaxCall = true).async { implicit request =>
    request.getQueryString("id") match {
      case Some(beforeId) => {
        val (token, secret) = userAccessContext.get
        (for {
          id <- Future { beforeId.toLong }
          timeline <- TwitterService(token, secret).homeTimelineBefore(id)
        } yield Ok(Json.toJson(timeline.map(StatusExt(_).toJson)))).recover {
          case e => InternalServerError("Cannot retrieve timeline")
        }
      }
      case None => Future { BadRequest("id parameter required") }
    }
  }

  def statusAfter = SecuredAction(ajaxCall = true).async { implicit request =>
    val afterId = request.getQueryString("id").getOrElse("0")
    val (token, secret) = userAccessContext.get
    (for {
      id <- Future { afterId.toLong }
      timeline <- TwitterService(token, secret).homeTimelineAfter(id)
    } yield Ok(Json.toJson(timeline.map(StatusExt(_).toJson)))).recover {
      case e => InternalServerError("Cannot retrieve timeline")
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
          val result = TwitterService(token, secret).createStatusUpdate(tweetData, photo)
          result match {
            case Success(_) => Redirect(routes.Client.home).flashing("success" -> "status update posted")
            case Failure(_) => Redirect(routes.Client.home).flashing("server-error" -> "failed to post status update")
          }
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

