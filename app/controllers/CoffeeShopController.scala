package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import entities.CoffeeShop
import io.swagger.annotations.{ Api, ApiParam, ApiResponse, ApiResponses }
import models.CoffeeShops
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.{ AbstractController, ControllerComponents }
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
@Api
class CoffeeShopController @Inject() (
    components: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    addToken: CSRFAddToken
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
      Future.successful(Ok(Json.toJson(CoffeeShops.findAll.map { coffeeShops =>
        CoffeeShop(
          coffeeShops.id, coffeeShops.name, coffeeShops.email,
          coffeeShops.ownerName, coffeeShops.address
        )
      })))
    })

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Shop not found")))
  def find(@ApiParam(value = "ID of the Coffee Shop to fetch") id: String) =
    silhouette.UnsecuredAction {
      CoffeeShops.find(id.toInt) match {
        case Some(coffeeShops) =>
          Ok(Json.toJson(
            CoffeeShop(
              coffeeShops.id, coffeeShops.name, coffeeShops.email,
              coffeeShops.ownerName, coffeeShops.address)
          ))

        case _ =>
          NotFound(JsObject.empty)
      }
    }

}
