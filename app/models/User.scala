package io.bimo2.stackshare

import play.api.libs.functional.syntax._
import play.api.libs.json._

class User(var username: String, var attributes: Map[String, Int]) {}

object User {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsObject = {
      Json.obj(
        "username" -> user.username,
        "attributes" -> user.attributes
      )
    }
  }

  implicit val userReads = (
    (JsPath \ "username").read[String] and
    (JsPath \ "attributes").read[Map[String, Int]]
  )(User.apply _)

  def apply(username: String, attributes: Map[String, Int]): User = {
    new User(username, attributes)
  }
}
