package controllers

import javax.inject.Inject

import play.api.libs.json.{ JsObject, Json }
import com.mohiva.play.silhouette.api.Silhouette
import models.CoffeeBeans
import entities.CoffeeBean
import io.swagger.annotations.{ Api, ApiParam, ApiResponse, ApiResponses }
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.filters.csrf.CSRFAddToken
import utils.auth.DefaultEnv

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
    addToken: CSRFAddToken
)(
    implicit
    webJarsUtil: WebJarsUtil,
    assets: AssetsFinder
) extends AbstractController(components) with I18nSupport {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Coffee Bean not found")))
  def find(@ApiParam(value = "ID of the Coffee Bean to fetch") id: String) =
    silhouette.UnsecuredAction {
      CoffeeBeans.find(id.toInt) match {
        case Some(coffeeBeans) =>
          Ok(Json.toJson(CoffeeBean(coffeeBeans.id, coffeeBeans.name, coffeeBeans.kind)))

        case _ =>
          //NotFound(new ApiResponse(404, "Coffee Bean not found"), 404)
          NotFound(JsObject.empty)
      }
    }

}
