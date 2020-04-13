package io.bimo2.stackshare

import org.mongodb.scala.Document

import play.api.libs.functional.syntax._
import play.api.libs.json._

class Company(val id: Option[String], val domain: String, var name: Option[String]) {

  override def toString(): String = {
    val idOption = id.getOrElse(None)
    val nameOption = name.getOrElse(None)

    s"[Company] ($idOption) $domain: $name"
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
