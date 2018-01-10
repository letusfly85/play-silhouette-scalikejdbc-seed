package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import entities.CoffeeBean
import models.{ CoffeeBeans, User }
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.{ BeforeAfterAll, Scope }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }

import scala.concurrent.ExecutionContext.Implicits.global
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

  "The `list` action" should {
    "return Ok if coffee beans found" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(GET, s"${routes.CoffeeBeanController.list().path()}?coffee-shop-id=1")
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)

        val expectedResults = CoffeeBeans.findAll()
        expectedResults.nonEmpty must beEqualTo(true)
      }
    }

    "return Ok if coffee beans not found" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(GET, s"${routes.CoffeeBeanController.list().path()}?coffee-shop-id=9999")
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)

        val expectedResults = CoffeeBeans.findAll()
        expectedResults.isEmpty must beEqualTo(true)
      }
    }
  }

  "The `find` action" should {
    "return Ok if a coffee bean found" in new Context {
      new WithApplication(application) {
        val coffeeBeanId = "999"
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.find(coffeeBeanId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)
      }
    }

    "return NotFound if a coffee bean not found" in new Context {
      new WithApplication(application) {
        val coffeeBeanId = "1001"
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.find(coffeeBeanId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(NOT_FOUND)
      }
    }
  }

  "The `create` action" should {
    "return Created if coffee beans is created" in new Context {
      new WithApplication(application) {
        val requestBody = CoffeeBean(id = 0, name = Some("Sumatra"), kind = Some("East Asia"), coffeeShopId = Some(1))
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.create())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
          .withBody(Json.toJson(requestBody))
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(CREATED)
      }
    }

    "return BadRequest if post parameter is invalid" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.create())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
          .withBody(Json.obj("name" -> "Guatemara"))
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(BAD_REQUEST)
      }
    }

    "return BadRequest if request without json" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.create())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(BAD_REQUEST)
      }
    }
  }

  "The `update` action" should {
    "return Ok if coffee beans is updated" in new Context {
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

    "return NotFound error if coffee beans is not found" in new Context {
      new WithApplication(application) {
        val requestBody = CoffeeBean(id = 998, name = Some("guatemala"), kind = Some("latin america"), coffeeShopId = Some(1))
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.update())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
          .withBody(Json.toJson(requestBody))
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(NOT_FOUND)
      }
    }

    "return BadRequest if post parameter is invalid" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.update())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
          .withBody(Json.obj("name" -> "Guatemara"))
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(BAD_REQUEST)
      }
    }

    "return BadRequest if request without json" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.update())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(BAD_REQUEST)
      }
    }
  }

  "The `destroy` action" should {
    "return Ok if coffee beans is destroyed" in new Context {
      new WithApplication(application) {
        val destroyId = "234"
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.destroy(destroyId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)
      }
    }

    "return NotFound if coffee beans is not found" in new Context {
      new WithApplication(application) {
        val destroyId = "235"
        val Some(future) = route(app, FakeRequest(routes.CoffeeBeanController.destroy(destroyId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(NOT_FOUND)
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
