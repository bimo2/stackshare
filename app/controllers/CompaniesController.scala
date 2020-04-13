package io.bimo2.stackshare

import play.api.libs.json._
import play.api.mvc._

class CompaniesController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val companies = NoSQLService.findCompanies()

      Ok(views.html.companies(companies));
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500, e.getMessage()))
      }
    }
  }

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.new_company())
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val json = request.body.asJson.get
      val company = json.as[Company]

      NoSQLService.writeCompany(company)

      val response = Json.toJson(company)

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

  def destroy(id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      NoSQLService.dropCompany(id)

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
