package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import io.swagger.annotations.ApiResponses
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.JsObject
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import play.filters.csrf.CSRFCheck
import utils.auth.{ DefaultEnv, WithCredentialsProvider }

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class AdministratorController @Inject() (
    components: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    checkToken: CSRFCheck
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
  def index = silhouette.SecuredAction(WithCredentialsProvider("credentials")).async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.admin(request.identity)))
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val result = Redirect(routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  //TODO
  @ApiResponses(Array())
  def update =
    checkToken(silhouette.SecuredAction.async { implicit request =>
      Future.successful(Ok(JsObject.empty))
    })
}
