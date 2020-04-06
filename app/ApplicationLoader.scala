import io.bimo2.stackshare.Controller

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

  lazy val controller = new Controller(controllerComponents)
  lazy val router = new Routes(httpErrorHandler, controller, assets)
}
