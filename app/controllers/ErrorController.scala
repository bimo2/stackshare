package io.bimo2.stackshare

import play.api.mvc._

class ErrorController (val controllerComponents: ControllerComponents) extends BaseController {

  def show(status: Int) = Action { implicit request: Request[AnyContent] =>
    Status(status)(views.html.error(status))
  }
}
