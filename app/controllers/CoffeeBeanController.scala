package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.CoffeeBeans
import entities.CoffeeBean
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
import play.api.libs.json.{JsFalse, JsObject, JsSuccess, Json}
import utils.auth.DefaultEnv
import scalikejdbc._

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
class CoffeeBeanController @Inject() (
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
      val coffeeShopId = request.queryString.get("coffee-shop-id").headOption match {
        case Some(values) => values.head
        case None         => ""
      }

      Future.successful(Ok(Json.toJson(
        CoffeeBeans.findAllBy(sqls.eq(CoffeeBeans.column.coffeeShopId, coffeeShopId)).map { coffeeBeans =>
          CoffeeBean(
            coffeeBeans.id, coffeeBeans.name, coffeeBeans.kind, coffeeBeans.coffeeShopId
          )
        })))
    })

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Bean not found")))
  def find(@ApiParam(value = "ID of the Coffee Bean to fetch") id: String) =
    silhouette.SecuredAction { implicit request =>
      CoffeeBeans.find(id.toInt) match {
        case Some(coffeeBeans) =>
          Ok(Json.toJson(
            CoffeeBean(coffeeBeans.id, coffeeBeans.name, coffeeBeans.kind, coffeeBeans.coffeeShopId)
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
          Json.fromJson[CoffeeBean](json) match {
            case JsSuccess(coffeeBeans, _) =>
              CoffeeBeans.find(coffeeBeans.id) match {
                case Some(beans) =>
                  beans.copy(
                    name = coffeeBeans.name, kind = coffeeBeans.kind, coffeeShopId = coffeeBeans.coffeeShopId
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
}
