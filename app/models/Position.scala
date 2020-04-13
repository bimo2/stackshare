package io.bimo2.stackshare

import org.mongodb.scala.Document

import play.api.libs.functional.syntax._
import play.api.libs.json._

class Position(val id: Option[String], val url: String, val text: String, var domain: String, var attributes: Map[String, Int]) {

  override def toString(): String = {
    val idOption = id.getOrElse(None)
    val contentLength = text.length()

    s"[Position] ($idOption) $domain: $contentLength"
  }
}

object Position
  extends NoSQLModel[Position] {
  
  implicit val positionWrites = new Writes[Position] {
    def writes(position: Position): JsObject = {
      Json.obj(
        "_id" -> position.id,
        "url" -> position.url,
        "domain" -> position.domain,
        "attributes" -> position.attributes,
        "text" -> position.text
      )
    }
  }

  implicit val positionReads = (
    (JsPath \ "_id").readNullable[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "text").read[String] and
    (JsPath \ "domain").read[String] and
    (JsPath \ "attributes").read[Map[String, Int]]
  )(Position.apply _)

  def apply(id: Option[String], url: String, text: String, domain: String, attributes: Map[String, Int]): Position = {
    new Position(id, url, text, domain, attributes)
  }

  def toNoSQL(position: Position): Document = {
    Document(
      "url" -> position.url,
      "text" -> position.text,
      "domain" -> position.domain,
      "attributes" -> Document(position.attributes)
    )
  }

  def toModel(document: Document): Position = {
    val id = Option(document.get("_id").get.asObjectId().getValue().toString())
    val url = document.get("url").get.asString().getValue().toString()
    val text = document.get("text").get.asString().getValue().toString()
    val domain = document.get("domain").get.asString().getValue().toString()
    val attributesBson = Document(document.get("attributes").get.asDocument())

    val attributesSequence = attributesBson.toSeq.map { attribute =>
      attribute._1 -> attribute._2.asInt32().getValue()
    }

    val attributes = Map(attributesSequence: _*)

    new Position(id, url, text, domain, attributes)
  }
}
