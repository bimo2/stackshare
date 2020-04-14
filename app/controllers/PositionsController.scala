package io.bimo2.stackshare

import io.lemonlabs.uri.Url
import scala.collection.immutable.ListMap

import play.api.libs.json._
import play.api.mvc._

class PositionsController(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val positions = NoSQLService.findPositions()
      var attributes = Language.getDoubleMapping()

      positions.foreach { position =>
        for ((key, value) <- position.attributes) {
          attributes = attributes.updatedWith(key)(_.map(_ + value))
        }

        val positionAttributes = position.attributes.toSeq.sortWith(_._2 > _._2)
        position.attributes = ListMap(positionAttributes: _*).filter(_._2 > 0).take(3)
      }

      val popularMap = attributes.transform((key, value) => {
        value / positions.length
      })

      val popularSequence = popularMap.toSeq.sortWith(_._2 > _._2)
      val popular = Vector(popularSequence: _*).filter(_._2 > 0)

      Ok(views.html.positions(positions, popular));
    }
    catch {
      case e: Exception => {
        InternalServerError(views.html.error(500, e.getMessage()))
      }
    }
  }

  def show(id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val position = NoSQLService.findPosition(id)

      val attributes = position.attributes.toSeq.sortWith(_._2 > _._2)
      position.attributes = ListMap(attributes: _*).filter(_._2 > 0)

      Ok(views.html.position(position))
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

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val json = request.body.asJson.get
      val url = Url.parse((json \ "url").as[String])
      val domain = url.apexDomain.get
      val content = WebContentService.fetchSeq(url.toString)
      val title = content._1.getOrElse("")
      val description = content._2.getOrElse("")
      val text = content._3.mkString(" ")
      val attributes = Language.frequencyAnalysis(title, description, content._3)
      val position = new Position(None, url.toString, text, content._1, content._2, domain, attributes)

      NoSQLService.writePosition(position)

      val response = Json.toJson(position)

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

  def update(id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      val json = request.body.asJson.get
      val domain = (json \ "domain").as[String]

      NoSQLService.updatePositionDomain(id, domain)

      val message = Message(200)
      val response = Json.toJson(message)

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
      NoSQLService.dropPosition(id)

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
