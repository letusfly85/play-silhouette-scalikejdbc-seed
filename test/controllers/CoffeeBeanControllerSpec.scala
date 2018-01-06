package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import entities.CoffeeBean
import models.{ CoffeeBeans, User }
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.{ BeforeAfter, BeforeAfterAll, Scope }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.HttpEntity.Strict
import scalikejdbc._
import scalikejdbc.config.DBs
import utils.auth.DefaultEnv

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

/**
 * Test case for the [[controllers.ApplicationController]] class.
 */
class CoffeeBeanControllerSpec extends PlaySpecification with Mockito with BeforeAfterAll {
  sequential

  override def beforeAll {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_beans where id = ?").bind(999).update.apply()
      SQL("delete from coffee_beans where id = ?").bind(234).update.apply()
      SQL("insert into coffee_beans values (?, ?, ?, ?)").bind(999, "TestBean", "Latin America", 1).update.apply()
      SQL("insert into coffee_beans values (?, ?, ?, ?)").bind(234, "Ethiopia", "Africa", 1).update.apply()
    }
  }

  override def afterAll = {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_beans where id = ?").bind(999).update.apply()
      SQL("delete from coffee_beans where id = ?").bind(234).update.apply()
    }
  }

  "The `update` action" should {
    "return 200 if coffee beans is updated" in new Context {
      new WithApplication(application) {
        val requestBody = CoffeeBean(id = 999, name = Some("guatemala"), kind = Some("latin america"), coffeeShopId = Some(1))
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.update())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
          .withBody(Json.toJson(requestBody))
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)
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
