import play.api.http.HttpErrorHandler
import scala.concurrent.Future

import play.api.mvc._
import play.api.mvc.Results._

class ApplicationErrorHandler
  extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Status(statusCode)(views.html.error(statusCode)))
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(InternalServerError(views.html.error(500)))
  }
}
