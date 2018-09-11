package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import models.User
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.CSRFTokenHelper._
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Test case for the [[controllers.ApplicationController]] class.
 */
class ApplicationControllerSpec extends PlaySpecification with Mockito {
  sequential

  "The `index` action" should {
    "redirect to login page if user is unauthorized" in new Context {
      new WithApplication(application) {
        val Some(unAuthorizedResult) = route(app, FakeRequest(routes.ApplicationController.index())
          .withAuthenticator[DefaultEnv](LoginInfo("invalid", "invalid"))
        )

        status(unAuthorizedResult) must be equalTo UNAUTHORIZED
      }
    }

    "return 200 if user is authorized" in new Context {
      new WithApplication(application) {
        val Some(result) = route(app, addCSRFToken(FakeRequest(routes.ApplicationController.index())
          .withAuthenticator[DefaultEnv](identity.loginInfo))
        )

        status(result) must beEqualTo(OK)
      }
    }
  }

  "The `signOut` action" should {
    "return 200 if user sign out" in new Context {
      new WithApplication(application) {
        val Some(result) = route(app, FakeRequest(routes.ApplicationController.signOut())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )

        status(result) must beEqualTo(OK)
      }
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    /**
     * A fake Guice module.
     */
    class FakeModule extends AbstractModule with ScalaModule {
      def configure() = {
        bind[Environment[DefaultEnv]].toInstance(env)
      }
    }

    /**
     * An identity.
     */
    val identity = User(
      userID = UUID.randomUUID(),
      loginInfo = LoginInfo("facebook", "user@facebook.com"),
      role = "normal",
      firstName = None,
      lastName = None,
      fullName = None,
      email = None,
      avatarURL = None,
      activated = true
    )

    /**
     * A Silhouette fake environment.
     */
    implicit val env: Environment[DefaultEnv] = new FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))

    /**
     * The application.
     */
    lazy val application = new GuiceApplicationBuilder()
      .overrides(new FakeModule)
      .build()
  }
}
