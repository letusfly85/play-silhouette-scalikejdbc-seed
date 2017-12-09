package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class CoffeeShop(id: Int, name: Option[String], email: Option[String], ownerName: Option[String], address: Option[String])

object CoffeeShop {
  implicit def coffeeShopEntityReads: Reads[CoffeeShop] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "email").readNullable[String] and
    (JsPath \ "owner_name").readNullable[String] and
    (JsPath \ "address").readNullable[String]
  )(CoffeeShop.apply _)

  implicit def coffeeShopEntityWrites: Writes[CoffeeShop] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "email").writeNullable[String] and
    (JsPath \ "owner_name").writeNullable[String] and
    (JsPath \ "address").writeNullable[String]
  )(unlift(CoffeeShop.unapply))
}

