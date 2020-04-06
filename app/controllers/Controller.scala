package io.bimo2.stackshare

import play.api.mvc._

class Controller (val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
