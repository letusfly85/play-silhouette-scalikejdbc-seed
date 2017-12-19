package entities

import models.CoffeeKinds
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class CoffeeKind(id: Int, name: Option[String], description: Option[String])

object CoffeeKind {
  implicit def coffeeBeanEntityReads: Reads[CoffeeKind] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "description").readNullable[String]
  )(CoffeeKind.apply _)

  implicit def coffeeBeanEntityWrites: Writes[CoffeeKind] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "description").writeNullable[String]
  )(unlift(CoffeeKind.unapply))

  implicit def convertModelToEntity(coffeeBeans: CoffeeKinds): CoffeeKind = {
    CoffeeKind(coffeeBeans.id, coffeeBeans.name, coffeeBeans.description)
  }
}
