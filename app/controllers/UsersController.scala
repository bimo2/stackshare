package io.bimo2.stackshare

import play.api.mvc._
import play.api.libs.json._

class UsersController (val controllerComponents: ControllerComponents) extends BaseController {

  def add() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.new_user())
  }
}
