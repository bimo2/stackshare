package io.bimo2.stackshare

import play.api.libs.json._

case class Message(status: Int, message: String = "OK")

object Message {

  implicit val messageWrites = new Writes[Message] {
    def writes(message: Message): JsObject = {
      Json.obj(
        "status" -> message.status,
        "message" -> message.message
      )
    }
  }
}
