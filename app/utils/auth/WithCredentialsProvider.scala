package utils.auth

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import play.api.mvc.Request

import scala.concurrent.Future

case class WithCredentialsProvider(provider: String) extends Authorization[User, CookieAuthenticator] {

  def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B]) = {

    if (user.role == "admin") {
      println(user.role)
      Future.successful(true)

    } else {
      println(user.role)
      Future.successful(false)
    }
  }
}
