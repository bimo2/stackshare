package io.bimo2.stackshare

import scala.collection.immutable.ListMap

import play.api.libs.json._
import play.api.mvc._

class CompaniesController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val companies = NoSQLService.findCompanies()
      var attributes = Language.getDoubleMapping()

      companies.foreach { company =>
        val positions = NoSQLService.findCompanyPositions(company.domain)
        company.calculateStack(positions)

        val companyStack = company.stack.get.toSeq.sortWith(_._2 > _._2)
        company.stack = Option(ListMap(companyStack: _*).filter(_._2 > 0).take(3))

        for ((key, value) <- companyStack) {
          attributes = attributes.updatedWith(key)(_.map(_ + value))
        }
      }

      val popularMap = attributes.transform((key, value) => {
        value / companies.length
      })

      val popularSequence = popularMap.toSeq.sortWith(_._2 > _._2)
      val popular = Vector(popularSequence: _*).filter(_._2 > 0)

      Ok(views.html.companies(companies, popular));
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500, e.getMessage()))
      }
    }
  }

  def show(id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val company = NoSQLService.findCompany(id)
      val positions = NoSQLService.findCompanyPositions(id)

      company.calculateStack(positions)

      val stack = company.stack.get.toSeq.sortWith(_._2 > _._2)
      company.stack = Option(ListMap(stack: _*).filter(_._2 > 0))

      for (position <- positions) {
        val attributes = position.attributes.toSeq.sortWith(_._2 > _._2)
        position.attributes = ListMap(attributes: _*)
      }

      Ok(views.html.company(company, positions))
    }
    catch {
      case e: NoSuchElementException => {
        NotFound(views.html.error(404, e.getMessage()))
      }

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
