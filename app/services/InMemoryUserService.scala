package services

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.{Identity, IdentityId}


/**
 * Transient user service.
 *
 * Based on:
 * https://github.com/jaliss/securesocial/blob/master/samples/scala/demo/app/service/InMemoryUserService.scala
 */
class InMemoryUserService(application: Application) extends UserServicePlugin(application) {

  private var users = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    users.get(id.userId + id.providerId)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    users.values.find(u => u.email.map(e => e == email && u.identityId.providerId == providerId).getOrElse(false))
  }

  def save(user: Identity) = {
    users = users + (user.identityId.userId + user.identityId.providerId -> user)
    user
  }

  def save(token: Token): Unit = {
    tokens += (token.uuid -> token)
  }

  def findToken(token: String) = tokens.get(token)

  def deleteToken(uuid: String): Unit = {
    tokens -= uuid
  }

  def deleteTokens(): Unit = {
    tokens = Map()
  }

  def deleteExpiredTokens(): Unit = {
    tokens = tokens.filter(!_._2.isExpired)
  }

}

