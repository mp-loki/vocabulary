package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.{ MimeTypes, HeaderNames }
import play.api.libs.ws.WS
import play.api.mvc.{ Results, Action, Controller }
import play.api.libs.json._
import play.api.cache._
import play.mvc.Results._
import javax.inject._
import helpers.Auth0Config

class UserController @Inject() (cache: CacheApi, config: Auth0Config) extends Controller {

  def login = Action {
    request =>
      (request.session.get("idToken").flatMap { idToken =>
        cache.get[JsObject](idToken + "profile")
      } map { profile =>
        Ok(views.html.home(profile))
      }).orElse {
        Some(Ok(views.html.login(config)))
      }.get
  }

  def logout = Action {
    Redirect(routes.UserController.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        cache.get[JsObject](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Redirect(routes.UserController.login()))
      }.get
    }
  }

  def index = AuthenticatedAction { request =>
    val idToken = request.session.get("idToken").get
    val profile = cache.get[JsObject](idToken + "profile").get
    Ok(views.html.home(profile))
  }
}