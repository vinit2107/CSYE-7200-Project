package controllers

import javax.inject._
import play.api.mvc._
import login.UserDAO

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def login() = Action {implicit  request =>
    Ok(views.html.credentials.login())
  }

  def signin() = Action {implicit request =>
    val data = request.body.asFormUrlEncoded
    data.map { args =>
      val username = args.get("uname").get.head
      val password = args.get("password").get.head
      if(UserDAO.validateUser(username, password))
        Ok(s"$username logged in using $password")
      else
        Redirect(routes.HomeController.login()).flashing("error" -> "**Invalid Username or Password")
    }.getOrElse(Redirect(routes.HomeController.login()).flashing("error" -> "**Please complete all fields."))
  }

  def description() = Action { implicit request =>
    Ok(views.html.credentials.description())
  }

  def signup() = Action { implicit request =>
    Redirect(routes.HomeController.login())
  }
}
