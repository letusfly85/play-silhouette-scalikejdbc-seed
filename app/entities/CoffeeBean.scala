package entities

import models.CoffeeBeans
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class CoffeeBean(id: Int, name: Option[String], kind: Option[String], coffeeShopId: Option[Int])

object CoffeeBean {
  implicit def coffeeBeanEntityReads: Reads[CoffeeBean] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "kind").readNullable[String] and
    (JsPath \ "coffee_shop_id").readNullable[Int]
  )(CoffeeBean.apply _)

  implicit def coffeeBeanEntityWrites: Writes[CoffeeBean] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "kind").writeNullable[String] and
    (JsPath \ "coffee_shop_id").writeNullable[Int]
  )(unlift(CoffeeBean.unapply))

  implicit def convertModelToEntity(coffeeBeans: CoffeeBeans): CoffeeBean = {
    CoffeeBean(coffeeBeans.id, coffeeBeans.name, coffeeBeans.kind, coffeeBeans.coffeeShopId)
  }
}
