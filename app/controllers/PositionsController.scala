package io.bimo2.stackshare

import play.api.libs.json._
import play.api.mvc._

class PositionsController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val positions = NoSQLService.findPositions()

      Ok(views.html.positions(positions));
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500, e.getMessage()))
      }
    }
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val json = request.body.asJson.get
      val url = (json \ "url").as[String]
      val text = WebContentService.fetchSeq(url)
      val response = Json.toJson(text)

      Ok(response)
    }
    catch {
      case e: JsResultException => {
        val message = Message(400, e.getMessage())
        val response = Json.toJson(message)

        BadRequest(response)
      }

      case e: Exception => {
        val message = Message(500, e.getMessage())
        val response = Json.toJson(message)

        InternalServerError(response)
      }
    }
  }
}
