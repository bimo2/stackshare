package io.bimo2.stackshare

import io.lemonlabs.uri.Url

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
      val url = Url.parse((json \ "url").as[String])
      val domain = url.apexDomain.get
      val content = WebContentService.fetchSeq(url.toString)
      val text = content._3.mkString(" ")
      val position = new Position(None, url.toString, text, content._1, content._2, domain, Map[String, Int]())

      NoSQLService.writePosition(position)

      val response = Json.toJson(position)

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
