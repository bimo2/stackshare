package io.bimo2.stackshare

import play.api.mvc._

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

      Ok("200")
    }
    catch {
      case any: Throwable => InternalServerError("500")
    }
  }
}
