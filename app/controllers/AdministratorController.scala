package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import entities.UserRole
import io.swagger.annotations.ApiResponses
import models.Users
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, JsSuccess, Json}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import play.filters.csrf.CSRFCheck
import utils.auth.{DefaultEnv, WithCredentialsProvider}

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

  @ApiResponses(Array())
  def update =
    checkToken(silhouette.SecuredAction(WithCredentialsProvider("credentials")).async { implicit request =>
      request.body.asJson match {
        case Some(json) =>
          Json.fromJson[UserRole](json) match {
            case JsSuccess(userRole, _) =>
              Users.find(userRole.id) match {
                case Some(users) =>
                  users.copy(
                    role = userRole.role
                  ).save()

                case None =>
                  Future.successful(Ok(Json.toJson(userRole)))
              }

              Future.successful(Ok(Json.toJson(userRole)))

            case e =>
              //TODO
              println(e.toString)
              Future.successful(Ok(JsObject.empty))
          }

        case None =>
          //TODO
          Future.successful(Ok(JsObject.empty))
      }
      Future.successful(Ok(JsObject.empty))
    })
}
