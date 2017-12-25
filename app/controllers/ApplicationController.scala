package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import org.webjars.play.WebJarsUtil
import play.api.i18n._
import play.api.libs.json.{ JsObject, JsString }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents, Request }
import play.filters.csrf.CSRFAddToken
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class ApplicationController @Inject() (
    components: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    addToken: CSRFAddToken,
    langs: Langs,
    messagesApi: MessagesApi
)(
    implicit
    webJarsUtil: WebJarsUtil,
    assets: AssetsFinder
) extends AbstractController(components) with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = addToken(silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>
    val localeCode = request.queryString.get("client-locale").headOption match {
      case Some(values) => values.head
      case None         => "jp"
    }

    val lang: Lang = langs.availables.find { lang => lang.code == localeCode }.getOrElse(Lang("jp"))
    val title: String = messagesApi("home.title")(lang)

    val result = JsObject.apply(Seq("hello" -> JsString(title)))

    Future.successful(Ok(result))
  })

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val result = Ok(JsObject.empty)
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
