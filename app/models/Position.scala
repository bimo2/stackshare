package io.bimo2.stackshare

import org.mongodb.scala.Document
import scala.collection.immutable.ListMap

import play.api.libs.functional.syntax._
import play.api.libs.json._

class Position(val id: Option[String], val url: String, val text: String, var title: Option[String], var description: Option[String], var domain: String, var attributes: Map[String, Double]) {

  override def toString(): String = {
    val idOption = id.getOrElse(None)
    val titleOption = title.getOrElse(None)

    s"[Position] ($idOption) $domain: $titleOption"
  }

  def suggestUsers(users: Vector[User], max: Int): Vector[(User, Double)] = {
    var results = Vector[(User, Double)]()

    for (user <- users) {
      val vector = user.attributes.map {
        case (key, value) => {
          (key, value.toDouble)
        }
      }

      val score = Language.dotProduct(vector, attributes)
      results = results :+ (user, score)
    }

    results = results.toSeq.filter(_._2 > 0).sortWith(_._2 > _._2)
    
    Vector[(User, Double)]() ++ results.take(max)
  }
}

object Position
  extends NoSQLModel[Position] {
  
  implicit val positionWrites = new Writes[Position] {
    def writes(position: Position): JsObject = {
      Json.obj(
        "_id" -> position.id,
        "url" -> position.url,
        "text" -> position.text,
        "title" -> position.title,
        "description" -> position.description,
        "domain" -> position.domain,
        "attributes" -> position.attributes
      )
    }
  }

  implicit val positionReads = (
    (JsPath \ "_id").readNullable[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "text").read[String] and
    (JsPath \ "title").readNullable[String] and
    (JsPath \ "description").readNullable[String] and
    (JsPath \ "domain").read[String] and
    (JsPath \ "attributes").read[Map[String, Double]]
  )(Position.apply _)

  def apply(id: Option[String], url: String, text: String, title: Option[String], description: Option[String], domain: String, attributes: Map[String, Double]): Position = {
    new Position(id, url, text, title, description, domain, attributes)
  }

  def toNoSQL(position: Position): Document = {
    Document(
      "url" -> position.url,
      "text" -> position.text,
      "title" -> position.title,
      "description" -> position.description,
      "domain" -> position.domain,
      "attributes" -> Document(position.attributes)
    )
  }

  def toModel(document: Document): Position = {
    val id = Option(document.get("_id").get.asObjectId().getValue().toString())
    val url = document.get("url").get.asString().getValue().toString()
    val text = document.get("text").get.asString().getValue().toString()
    val hasTitle = document("title").isString()
    val title = if (hasTitle) Option(document.get("title").get.asString().getValue().toString()) else None
    val hasDescription = document("description").isString()
    val description = if (hasDescription) Option(document.get("description").get.asString().getValue().toString()) else None
    val domain = document.get("domain").get.asString().getValue().toString()
    val attributesBson = Document(document.get("attributes").get.asDocument())

    val attributesSequence = attributesBson.toSeq.map { attribute =>
      attribute._1 -> attribute._2.asDouble().getValue()
    }

    val attributes = Map(attributesSequence: _*)

    new Position(id, url, text, title, description, domain, attributes)
  }
}
