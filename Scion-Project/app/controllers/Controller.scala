package controllers

import javax.inject._
import login.LoginHandler
import models.DAO.UserTable
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

// Case class to validate login form
case class LoginForm(username: String, password: String)

// Case class to validate sign-up form
case class SignUpForm(name: String, email: String, city: String, username: String, password: String)

@Singleton
class HomeController @Inject()(val cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  val loginData = Form(mapping(
    "Username" -> nonEmptyText,
    "Password" -> nonEmptyText
  )(LoginForm.apply)(LoginForm.unapply))

  val signupData = Form(mapping(
    "Name" -> nonEmptyText,
    "Email" -> email,
    "City" -> nonEmptyText,
    "Username" -> nonEmptyText,
    "Password" -> nonEmptyText
  )(SignUpForm.apply)(SignUpForm.unapply))

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def login() = Action {implicit  request =>
    Ok(views.html.credentials.login(loginData))
  }

  def validateLogin() = Action { implicit request =>
    loginData.bindFromRequest.fold(
      formWithError => BadRequest(views.html.credentials.login(formWithError)),
      ld => {
        val handler = new LoginHandler() with UserTable
        if (handler.validateUser(ld.username, ld.password))
          Ok(s"$ld.username logged in using $ld.password")
        else
          Redirect(routes.HomeController.login()).flashing("error" -> "**Invalid Username or Password")
      })
  }

  def signup() = Action { implicit request =>
    Ok(views.html.credentials.signup(signupData))
  }

  def validateSignUp() = Action {implicit request =>
    signupData.bindFromRequest.fold(
      formWithError => BadRequest(views.html.credentials.signup(formWithError)),
      sgd => {
        val handler = new LoginHandler() with UserTable
        if (handler.addUser(sgd.username, sgd.password, sgd.name, sgd.email, sgd.city))
          Redirect(routes.HomeController.login())
        else
          Redirect(routes.HomeController.signup()).flashing("error" -> "**UserName already exists")
      })
  }

}
