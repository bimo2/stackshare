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
}
