package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import models.AuthUser
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.{ BeforeAfterAll, Scope }
import play.api.inject.guice.GuiceApplicationBuilder
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
class CoffeeShopControllerSpec extends PlaySpecification with Mockito with BeforeAfterAll {
  sequential

  override def beforeAll {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_shops where id = ?").bind(999).update.apply()
      SQL("delete from coffee_shops where id = ?").bind(1000).update.apply()
      SQL("insert into coffee_shops values (?, ?, ?, ?, ?)")
        .bind(
          999, "Sge Backs", "sge-backs@example.com", "sge john", "somewhere"
        ).update.apply()
      SQL("insert into coffee_shops values (?, ?, ?, ?, ?)")
        .bind(
          1000, "Tul is", "tul-is@example.com", "tul sarah", "anywhere"
        ).update.apply()
    }
  }

  override def afterAll = {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_shops where id = ?").bind(999).update.apply()
      SQL("delete from coffee_shops where id = ?").bind(1000).update.apply()
    }
  }

  "The `list` action" should {
    "return Ok if coffee shops found" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeShopController.list())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)
      }
    }
  }

  "The `find` action" should {
    "return Ok if a coffee shop found" in new Context {
      new WithApplication(application) {
        val coffeeShopId = "1000"
        val Some(future) = route(app, FakeRequest(routes.CoffeeShopController.find(coffeeShopId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )
        val result = Await.result(future.asInstanceOf[Future[Result]], 3.seconds)

        result.header.status must beEqualTo(OK)
      }
    }

    "return NotFound if a coffee shop not found" in new Context {
      new WithApplication(application) {
        val coffeeShopId = "1001"
        val Some(future) = route(app, FakeRequest(routes.CoffeeShopController.find(coffeeShopId))
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
    val identity = AuthUser(
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
