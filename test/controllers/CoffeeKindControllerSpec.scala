package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.test._
import models.User
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.{ BeforeAfterAll, Scope }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }

import scala.concurrent.ExecutionContext.Implicits.global
import scalikejdbc._
import scalikejdbc.config.DBs
import utils.auth.DefaultEnv

/**
 * Test case for the [[controllers.ApplicationController]] class.
 */
class CoffeeKindControllerSpec extends PlaySpecification with Mockito with BeforeAfterAll {
  sequential

  override def beforeAll {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_kinds where id = ?").bind(999).update.apply()
      SQL("delete from coffee_kinds where id = ?").bind(1000).update.apply()
      SQL("insert into coffee_kinds values (?, ?, ?)")
        .bind(
          999, "Robusta", "made from the Coffea canephora plant, a sturdy species of coffee bean with low acidity and high bitterness."
        ).update.apply()
      SQL("insert into coffee_kinds values (?, ?, ?)")
        .bind(
          1000, "Arabica", "arabica is believed to be the first species of coffee to be cultivated, and is the dominant cultivar, representing some 60% of global production"
        ).update.apply()
    }
  }

  override def afterAll = {
    DBs.setupAll()
    DB autoCommit { implicit session =>
      SQL("delete from coffee_kinds where id = ?").bind(999).update.apply()
      SQL("delete from coffee_kinds where id = ?").bind(1000).update.apply()
    }
  }

  "The `list` action" should {
    "return Ok if coffee kinds found" in new Context {
      new WithApplication(application) {
        val Some(future) = route(app, FakeRequest(routes.CoffeeKindController.list())
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )

        status(future) must beEqualTo(OK)
      }
    }
  }

  "The `find` action" should {
    "return Ok if a coffee kind found" in new Context {
      new WithApplication(application) {
        val coffeeKindId = "1000"
        val Some(future) = route(app, FakeRequest(routes.CoffeeKindController.find(coffeeKindId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )

        status(future) must beEqualTo(OK)
      }
    }

    "return NotFound if a coffee kind not found" in new Context {
      new WithApplication(application) {
        val coffeeKindId = "1001"
        val Some(future) = route(app, FakeRequest(routes.CoffeeKindController.find(coffeeKindId))
          .withAuthenticator[DefaultEnv](identity.loginInfo)
        )

        status(future) must beEqualTo(NOT_FOUND)
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
