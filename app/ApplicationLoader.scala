import io.bimo2.stackshare.IndexController

import controllers.AssetsComponents
import play.api.ApplicationLoader.Context
import play.filters.HttpFiltersComponents
import router.Routes

import play.api._

class StackshareApplicationLoader extends ApplicationLoader {

  def load(context: Context) = {
    new Stackshare(context).application
  }
}

class Stackshare(context: Context) extends BuiltInComponentsFromContext(context) 
  with HttpFiltersComponents 
  with AssetsComponents {

  override lazy val httpErrorHandler = new ApplicationErrorHandler()

  lazy val indexController = new IndexController(controllerComponents)
  lazy val router = new Routes(httpErrorHandler, indexController, assets)
}
