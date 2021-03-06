package io.bimo2.stackshare

import play.api.libs.json._
import play.api.mvc._

class IndexController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val metrics = NoSQLService.getMetrics()

      Ok(views.html.index(metrics))
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500, e.getMessage()))
      }
    }
  }

  def destroy(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      NoSQLService.destroy()

      val message = Message(200)
      val response = Json.toJson(message)

      Ok(response)
    }
    catch {
      case e: Exception => {
        val message = Message(500, e.getMessage())
        val response = Json.toJson(message)

        InternalServerError(response)
      }
    }
  }
}
