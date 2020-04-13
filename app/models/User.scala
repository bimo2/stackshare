package io.bimo2.stackshare

import org.mongodb.scala.Document

import play.api.libs.functional.syntax._
import play.api.libs.json._

class User(val id: Option[String], val username: String, var attributes: Map[String, Int]) {

  override def toString(): String = {
    val idOption = id.getOrElse(None)

    s"[User] ($idOption) $username"
  }
}

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

  def toModel(document: Document): User = {
    val id = Option(document.get("_id").get.asObjectId().getValue().toString())
    val username = document.get("username").get.asString().getValue().toString()
    val attributesBson = Document(document.get("attributes").get.asDocument())

    val attributesSequence = attributesBson.toSeq.map { attribute =>
      attribute._1 -> attribute._2.asInt32().getValue()
    }

    val attributes = Map(attributesSequence: _*)

    new User(id, username, attributes)
  }
}
