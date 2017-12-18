package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class UserRole(id: Int, role: Option[String])

object UserRole {
  implicit def userEntityReads: Reads[UserRole] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "role").readNullable[String]
  )(UserRole.apply _)

  implicit def userEntityWrites: Writes[UserRole] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "role").writeNullable[String]
  )(unlift(UserRole.unapply))
}
