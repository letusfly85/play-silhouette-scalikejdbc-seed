package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object CoffeeBeanEntity {
  case class CoffeeBean(id: Int, name: Option[String], kind: Option[String])

  implicit val coffeeBeanEntityReads: Reads[CoffeeBean] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "kind").readNullable[String]
  )(CoffeeBean.apply _)

  implicit val coffeeBeanEntityWrites: Writes[CoffeeBean] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "kind").writeNullable[String]
  )(unlift(CoffeeBean.unapply))
}
