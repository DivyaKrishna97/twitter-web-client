package controllers

import securesocial.controllers.TemplatesPlugin
import securesocial.core.{SecuredRequest, Identity}
import play.api.mvc.{Flash, Request, RequestHeader}
import play.api.data.Form
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.Registration.RegistrationInfo

class AuthenticationTemplatesPlugin(application: play.Application) extends TemplatesPlugin {
  def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)], msg: Option[String]) = {
    views.html.index()(None, new Flash())
  }

  def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String) = ???

  def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]) = ???

  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String) = ???

  def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]) = ???

  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]) = ???

  def getNotAuthorizedPage[A](implicit request: Request[A]) = ???

  def getSignUpEmail(token: String)(implicit request: RequestHeader) = ???

  def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader) = ???

  def getWelcomeEmail(user: Identity)(implicit request: RequestHeader) = ???

  def getUnknownEmailNotice()(implicit request: RequestHeader) = ???

  def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader) = ???

  def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader) = ???

}
