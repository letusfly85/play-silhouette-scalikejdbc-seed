package entities

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class User(id: Int, email: String, role: String)

object User {
  implicit def userEntityReads: Reads[User] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "email").read[String] and
    (JsPath \ "role").read[String]
    )(User.apply _)

  implicit def userEntityWrites: Writes[User] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "email").write[String] and
    (JsPath \ "role").write[String]
    )(unlift(User.unapply))
}

