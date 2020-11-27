package controllers

import javax.inject._
import login.LoginHandler
import models.DAO.UserTable
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.util.{Failure, Success}

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

  def validateLogin(): Action[AnyContent] = Action { implicit request =>
    loginData.bindFromRequest.fold(
      formWithError => BadRequest(views.html.credentials.login(formWithError)),
      ld => {
         val handler = new LoginHandler() with UserTable
         (handler.validateUser(ld.username, ld.password)) match {
           case Success(_) => Ok(s"$ld.username logged in using $ld.password")
           case Failure(e) => Redirect(routes.HomeController.login()).flashing("error" -> s"**$e.getMessage")
      }
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
        (handler.addUser(sgd.username, sgd.password, sgd.name, sgd.email, sgd.city)) match {
          case Success(_) => Redirect(routes.HomeController.login())
          case Failure(e) => Redirect(routes.HomeController.signup()).flashing("error" -> s"**${e.getMessage}")
      }
  })
}
}
