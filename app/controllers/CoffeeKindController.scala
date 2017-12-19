package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import entities.CoffeeKind
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import models.CoffeeKinds
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
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
@Api
class CoffeeKindController @Inject()(
    components: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    addToken: CSRFAddToken,
    checkToken: CSRFCheck,
)(
    implicit
    webJarsUtil: WebJarsUtil,
    assets: AssetsFinder
) extends AbstractController(components) with I18nSupport {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Bean not found")))
  def list() =
    addToken(silhouette.SecuredAction.async { implicit request =>
      Future.successful(Ok(Json.toJson(CoffeeKinds.findAll.map{coffeeKinds =>
        CoffeeKind(coffeeKinds.id, coffeeKinds.name, coffeeKinds.description)
      })))
    })

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Bean not found")))
  def find(@ApiParam(value = "ID of the Coffee Bean to fetch") id: String) =
    silhouette.SecuredAction { implicit request =>
      CoffeeKinds.find(id.toInt) match {
        case Some(coffeeKinds) =>
          Ok(Json.toJson(
            CoffeeKind(coffeeKinds.id, coffeeKinds.name, coffeeKinds.description)
          ))

        case _ =>
          NotFound(JsObject.empty)
      }
    }

  @ApiResponses(Array())
  def update =
    checkToken(silhouette.SecuredAction.async { implicit request =>
      request.body.asJson match {
        case Some(json) =>
          Json.fromJson[CoffeeKind](json) match {
            case JsSuccess(coffeeBeans, _) =>
              CoffeeKinds.find(coffeeBeans.id) match {
                case Some(beans) =>
                  beans.copy(
                    name = coffeeBeans.name, description = coffeeBeans.description
                  ).save()

                case None =>
                  Future.successful(Ok(Json.toJson(coffeeBeans)))
              }

              Future.successful(Ok(Json.toJson(coffeeBeans)))

            case e =>
              //TODO
              println(e.toString)
              Future.successful(Ok(JsObject.empty))
          }

        case None =>
          //TODO
          Future.successful(Ok(JsObject.empty))
      }
    })

  @ApiResponses(Array())
  def create =
    checkToken(silhouette.SecuredAction.async { implicit request =>
      request.body.asJson match {
        case Some(json) =>
          Json.fromJson[CoffeeKind](json) match {
            case JsSuccess(coffeeBeans, _) =>
              CoffeeKinds.create(
                name = coffeeBeans.name,
                description = coffeeBeans.description
              ).save()

              Future.successful(Ok(Json.toJson(coffeeBeans)))

            case e =>
              //TODO
              println(e.toString)
              Future.successful(Ok(JsObject.empty))
          }

        case None =>
          //TODO
          Future.successful(Ok(JsObject.empty))
      }
    })

  @ApiResponses(Array())
  def destroy(@ApiParam(value = "ID of the Coffee Bean to fetch") id: String) =
    checkToken(silhouette.SecuredAction.async { implicit request =>
      CoffeeKinds.find(id.toInt) match {
        case Some(beans) =>
          beans.destroy()
          Future.successful(Ok(JsObject.empty))

        case None =>
          Future.successful(NotFound(Json.toJson(id)))
      }
    })
}
