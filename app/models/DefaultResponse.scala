package io.bimo2.stackshare

import play.api.libs.json._

case class DefaultResponse(status: Int)

object DefaultResponse {

  implicit val stringify: Format[DefaultResponse] = Json.format
}
