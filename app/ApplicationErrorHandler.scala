import io.bimo2.stackshare.Errors

import play.api.http.HttpErrorHandler
import scala.concurrent.Future

import play.api.mvc._
import play.api.mvc.Results._

class ApplicationErrorHandler
  extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val message = Errors.defaultMessage(statusCode)

    Future.successful(Status(statusCode)(views.html.error(statusCode, message)))
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val message = Errors.defaultMessage(500)

    Future.successful(InternalServerError(views.html.error(500, message)))
  }
}
