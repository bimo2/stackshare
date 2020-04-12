package io.bimo2.stackshare

import org.mongodb.scala.Document

import play.api.libs.functional.syntax._
import play.api.libs.json._

class User(val id: Option[String], var username: String, var attributes: Map[String, Int]) {}

object User
  extends NoSQLModel[User] {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsObject = {
      Json.obj(
        "_id" -> user.id,
        "username" -> user.username,
        "attributes" -> user.attributes
      )
    }
  }

  implicit val userReads = (
    (JsPath \ "_id").readNullable[String] and
    (JsPath \ "username").read[String] and
    (JsPath \ "attributes").read[Map[String, Int]]
  )(User.apply _)

  def apply(id: Option[String], username: String, attributes: Map[String, Int]): User = {
    new User(id, username, attributes)
  }

  def toNoSQL(user: User): Document = {
    Document(
      "username" -> user.username,
      "attributes" -> Document(user.attributes)
    )
  }
}
