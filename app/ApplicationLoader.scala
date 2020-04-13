import io.bimo2.stackshare._

import controllers.AssetsComponents
import play.api.ApplicationLoader.Context
import play.api.mvc.EssentialFilter
import play.filters.csrf.CSRFFilter
import play.filters.HttpFiltersComponents
import router.Routes

import play.api._

class StackshareApplicationLoader()
  extends ApplicationLoader {

  def load(context: Context): Application = {
    new Stackshare(context).application
  }
}

class Stackshare(context: Context)
  extends BuiltInComponentsFromContext(context) 
  with HttpFiltersComponents 
  with AssetsComponents {

  override lazy val httpErrorHandler = new ApplicationErrorHandler()

  lazy val indexController = new IndexController(controllerComponents)
  lazy val usersController = new UsersController(controllerComponents)
  lazy val companiesController = new CompaniesController(controllerComponents)
  lazy val errorController = new ErrorController(controllerComponents)

  lazy val router = new Routes(
    httpErrorHandler,
    indexController,
    usersController,
    companiesController,
    errorController,
    assets
  )

  override def httpFilters: Seq[EssentialFilter] = {
    super.httpFilters.filterNot(_.getClass == classOf[CSRFFilter])
  }
}
