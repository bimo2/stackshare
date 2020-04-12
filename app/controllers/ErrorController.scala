package io.bimo2.stackshare

import play.api.mvc._

class ErrorController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def show(status: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val message = Errors.defaultMessage(status)

    Status(status)(views.html.error(status, message))
  }
}
