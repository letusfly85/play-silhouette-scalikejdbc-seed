package utils.auth

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.AuthUser
import play.api.mvc.Request

import scala.concurrent.Future

case class WithCredentialsProvider(provider: String) extends Authorization[AuthUser, CookieAuthenticator] {

  def isAuthorized[B](user: AuthUser, authenticator: CookieAuthenticator)(implicit request: Request[B]) = {
    if (user.role == "admin") {
      Future.successful(true)

    } else {
      Future.successful(false)
    }
  }
}
