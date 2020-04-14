package io.bimo2.stackshare

import org.mongodb.scala.Document
import scala.collection.immutable.ListMap

import play.api.libs.functional.syntax._
import play.api.libs.json._

class Company(val id: Option[String], val domain: String, var name: Option[String], var stack: Option[Map[String, Double]] = None) {

  override def toString(): String = {
    val idOption = id.getOrElse(None)
    val nameOption = name.getOrElse(None)

    s"[Company] ($idOption) $domain: $name"
  }

  def calculateStack(positions: Vector[Position]): Map[String, Double] = {
    var attributes = Language.getDoubleMapping()
    var count = 0

    positions.foreach { position =>
      if (position.domain == domain) {
        for ((key, value) <- position.attributes) {
          attributes = attributes.updatedWith(key)(_.map(_ + value))
        }

        count += 1
      }
    }

    val stackAverage = attributes.filter(_._2 > 0).map {
      case (key, value) => {
        (key, value / count)
      }
    }

    stack = Option(stackAverage)

    stack.get
  }
}

object Company
  extends NoSQLModel[Company] {
  
  implicit val companyWrites = new Writes[Company] {
    def writes(company: Company): JsObject = {
      Json.obj(
        "_id" -> company.id,
        "domain" -> company.domain,
        "name" -> company.name
      )
    }
  }

  implicit val companyReads = (
    (JsPath \ "_id").readNullable[String] and
    (JsPath \ "domain").read[String] and
    (JsPath \ "name").readNullable[String]
  )(Company.apply _)

  def apply(id: Option[String], domain: String, name: Option[String]): Company = {
    new Company(id, domain, name)
  }

  def toNoSQL(company: Company): Document = {
    Document(
      "domain" -> company.domain,
      "name" -> company.name
    )
  }

  def toModel(document: Document): Company = {
    val id = Option(document.get("_id").get.asObjectId().getValue().toString())
    val domain = document.get("domain").get.asString().getValue().toString()

    val hasName = document("name").isString()
    val name = if (hasName) Option(document.get("name").get.asString().getValue().toString()) else None

    new Company(id, domain, name)
  }
}
