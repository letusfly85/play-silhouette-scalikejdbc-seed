package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import models.{ User, Users }
import net.codingwell.scalaguice.ScalaModule
import org.specs2.specification.{ BeforeAfterAll, Scope }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }
import scalikejdbc.config.DBs
import scalikejdbc.{ DB, SQL }
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Test case for the [[controllers.ApplicationController]] class.
 */
class SignInControllerSpec extends PlaySpecification with BeforeAfterAll {
  sequential
  DBs.setupAll()

  override def beforeAll = {
    val userIntId = 1
    val userUUID = UUID.randomUUID().toString
    val hashedPassword = "$2a$10$KkP3TOIqPCFlySAAW4kDyeYADo0pJkYxTGNN9OiShzsvUc2soBYOK" //hogehoge
    Users.find(userIntId).foreach(_.destroy())
    DB localTx { implicit session =>
      SQL("DELETE FROM users WHERE id = ?").bind(userIntId).update().apply()
      SQL(
        "insert into users (id, user_id, hasher, salt, password, first_name, last_name, email, avatar_url, activated) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
        .bind(
          userIntId,
          userUUID,
          "bcrypt-sha256",
          null,
          hashedPassword,
          "first_name",
          "last_name",
          "test@dancer-hard.io",
          "avatar_url",
          1)
        .update()
        .apply()
    }
  }

  override def afterAll = {}

  "The `index` action" should {
    "redirect to login page if user is unauthorized" in new Context {
      new WithApplication(application) {
        val jsonParameter = Map("email" -> "test@dancer-hard.io", "password" -> "hogehoge", "rememberMe" -> "true")
        val Some(result) = route(app, FakeRequest(routes.SignInController.submit())
          .withJsonBody(Json.toJson(jsonParameter))
        )

        status(result) must be equalTo CREATED
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
