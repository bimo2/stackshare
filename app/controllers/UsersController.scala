package io.bimo2.stackshare

import play.api.libs.json._
import play.api.mvc._

class UsersController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val users = NoSQLService.findUsers()
      var attributes = Language.toDoubleMap()

      users.foreach { user =>
        for ((key, value) <- user.attributes) {
          attributes = attributes.updatedWith(key)(_.map(_ + value))
        }
      }

      val averages = attributes.transform((key, value) => {
        value / users.length
      })

      val averagesSequence = averages.toSeq.sortWith(_._2 > _._2)
      val averagesVector = Vector(averagesSequence: _*).filter(_._2 > 0)

      Ok(views.html.users(users, averagesVector));
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500))
      }
    }
  }

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.new_user())
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val json = request.body.asJson.get
      val user = json.as[User]

      NoSQLService.writeUser(user)

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
