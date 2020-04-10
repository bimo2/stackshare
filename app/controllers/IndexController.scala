package io.bimo2.stackshare

import play.api.mvc._
import play.api.libs.json._

class IndexController (val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    try {
      val metrics = NoSQLService.getMetrics()

      Ok(views.html.index(metrics))
    }
    catch {
      case any: Throwable => InternalServerError(views.html.error(500))
    }
  }

  def destroy() = Action { implicit request: Request[AnyContent] =>
    try {
      NoSQLService.destroy()
      val json = Json.toJson(DefaultResponse(200))

      Ok(json)
    }
    catch {
      case any: Throwable => {
        val json = Json.toJson(DefaultResponse(500))

        InternalServerError(json)
      }
    }
  }
}
