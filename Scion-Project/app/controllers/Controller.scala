package controllers

import akka.util.Helpers.Requiring
import javax.inject._
import models.DAO.{StocksTable, UserStockTable, UserTable}
import models.StockSelection.{ListStocks, StockSelectionHandler}
import models.login.LoginHandler
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Case class to validate login form
case class LoginForm(username: String, password: String)

// Case class to validate sign-up form
case class SignUpForm(name: String, email: String, city: String, username: String, password: String)

// Case class to stock select form
case class StocklistForm(username: String, stockname: String)

// Case class to select stock
case class SelectedStocks(stocklist: String)

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

  val stocklistData = Form(mapping(
    "Stockname" -> nonEmptyText,
    "Shortname" ->  nonEmptyText)
  (StocklistForm.apply)(StocklistForm.unapply))

  val selectedstocks = Form(mapping(
    "selectedstocks" -> text
  )(SelectedStocks.apply)(SelectedStocks.unapply))

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def login(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.credentials.login(loginData))
  }

  def addRemoveStocks():Action[AnyContent] = Action { implicit request =>
    Ok(views.html.stockselect(stocklistData))
  }

  def validateLogin(): Action[AnyContent] = Action.async { implicit request =>
    loginData.bindFromRequest.fold(
      formWithError => Future(BadRequest(views.html.credentials.login(formWithError))),
      ld => {
         val handler = new LoginHandler() with UserTable
         (handler.validateUser(ld.username, ld.password)).map(b => b match {
           case true => Redirect(routes.HomeController.action()).withSession("username" -> ld.username)
           case false => Redirect(routes.HomeController.login()).flashing("error" -> s"**Username or password is incorrect")
      })
      })
  }

  def signup(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.credentials.signup(signupData))
  }

  def validateSignUp(): Action[AnyContent] = Action.async { implicit request =>
    signupData.bindFromRequest.fold(
      formWithError => Future(BadRequest(views.html.credentials.signup(formWithError))),
      sgd => {
        val handler = new LoginHandler() with UserTable
        (handler.addUser(sgd.username, sgd.password, sgd.name, sgd.email, sgd.city)).map(b => b match {
          case true => Redirect(routes.HomeController.login())
          case false => Redirect(routes.HomeController.signup()).flashing("error" -> s"**Username already exists")
      })
  })
  }

  def action(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.Action.action()).withSession("username" -> request.session.data.get("username").get)
  }

  def listStocks(): Action[AnyContent] = Action.async { implicit request =>
    val stocksList = new ListStocks() with StocksTable
    val allStocks = stocksList.listAllStocks()
    val stocksSelection = new StockSelectionHandler() with UserStockTable
    val selectedStocks = stocksSelection.fetch_current_stocks(request.session.get("username").get)
    for {
      as <- allStocks
      ss <- selectedStocks
    } yield Ok(views.html.listStocks(selectedstocks, as, ss)).withSession("username" -> request.session.data.get("username").get)
  }

  def saveStocks(): Action[AnyContent] = Action { implicit request =>
    selectedstocks.bindFromRequest.fold(
      fail => {
        Redirect(routes.HomeController.listStocks()).withSession("username" -> request.session.data.get("username").get)
      },
      success => {
        val stockHandler = new StockSelectionHandler() with UserStockTable
        stockHandler.store_new_stocks(request.session.get("username").get, success.stocklist)
        Redirect(routes.HomeController.listStocks()).withSession("username" -> request.session.data.get("username").get)
      }
    )
  }

  def listCommonStocks(): Action[AnyContent] = Action.async { implicit request =>
    val stocksList = new ListStocks() with StocksTable
    val allStocks = stocksList.listAllStocks()
    val stocksSelection = new StockSelectionHandler() with UserStockTable
    val selectedStocks = stocksSelection.fetch_current_stocks(request.session.get("username").get)
    for {
      as <- allStocks
      ss <- selectedStocks
    } yield Ok(views.html.removeStocks(selectedstocks, as, ss)).withSession("username" -> request.session.data.get("username").get)
  }

  def removeStocks(): Action[AnyContent] = Action { implicit request =>
    selectedstocks.bindFromRequest.fold(
      fail => {
        Redirect(routes.HomeController.listCommonStocks()).withSession("username" -> request.session.data.get("username").get)
      },
      success => {
        val stockHandler = new StockSelectionHandler() with UserStockTable
        stockHandler.delete_new_stocks(request.session.get("username").get, success.stocklist)
        Redirect(routes.HomeController.listCommonStocks()).withSession("username" -> request.session.data.get("username").get)
      })
  }
}
