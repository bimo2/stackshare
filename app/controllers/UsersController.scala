package io.bimo2.stackshare

import play.api.libs.json._
import play.api.mvc._

class UsersController(val controllerComponents: ControllerComponents) extends BaseController {

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.new_user())
  }

  def create(): Action[JsValue] = Action(parse.json) { implicit request: Request[JsValue] =>
    try {
      val json = request.body
      val user = json.as[User]
      val response = Json.toJson(user)

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
