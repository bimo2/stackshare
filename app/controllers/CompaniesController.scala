package io.bimo2.stackshare

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
}
