package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class UserRole(id: Int, role: String)

object UserRole {
  implicit def userRoleEntityReads: Reads[UserRole] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "role").read[String]
  )(UserRole.apply _)

  implicit def userRoleEntityWrites: Writes[UserRole] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "role").write[String]
  )(unlift(UserRole.unapply))
}
