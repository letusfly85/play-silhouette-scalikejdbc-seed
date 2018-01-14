package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import entities.{ User, UserRole }
import io.swagger.annotations.{ ApiResponse, ApiResponses }
import models.Users
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.{ JsError, JsObject, JsSuccess, Json }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import play.filters.csrf.{ CSRFAddToken, CSRFCheck }
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
    checkToken: CSRFCheck,
    addToken: CSRFAddToken
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
    Future.successful(Ok(JsObject.empty))
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

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Bean not found")))
  def list() =
    addToken(silhouette.SecuredAction.async { implicit request =>
      Future.successful(Ok(Json.toJson(Users.findAll.map { users =>
        User(
          users.id, users.email, users.role
        )
      })))
    })

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
                  Future.successful(Ok(Json.toJson(userRole)))

                case None =>
                  Future.successful(NotFound(Json.toJson(userRole)))
              }

            case JsError(e) =>
              Future.successful(BadRequest(Json.obj("error_message" -> JsError.toJson(e).toString())))
          }

        case None =>
          Future.successful(BadRequest(Json.obj("error_message" -> "not found json")))
      }
    })
}
