package controllers

import org.scalatestplus.play._
import play.api.test.{CSRFTokenHelper, FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import com.typesafe.config.ConfigFactory
import play.api.Configuration

class ControllerTestSpec extends PlaySpec {

  "application index" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.index.apply(CSRFTokenHelper.addCSRFToken(FakeRequest()))
      val bodyText = contentAsString(result)
      bodyText must include("Welcome")
      bodyText must include("TRADE WHAT YOU SEE,")
      bodyText must include("NOT WHAT YOU THINK!")
    }
  }

  "application login" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.login.apply(CSRFTokenHelper.addCSRFToken(FakeRequest()))
      val bodyText = contentAsString(result)
      bodyText must include("Your username")
      bodyText must include("Your password")
    }
  }

  "application signup" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.signup.apply(CSRFTokenHelper.addCSRFToken(FakeRequest()))
      val bodyText = contentAsString(result)
      bodyText must include("First Name & Last Name")
    }
  }

  "application action" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.action.apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username"-> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("Add Ticks")
      bodyText must include("Remove Ticks")
      bodyText must include("Historical Data Transformation")
    }
  }

  "application addRemoveStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.addRemoveStocks.apply(CSRFTokenHelper.addCSRFToken(FakeRequest()))
      val bodyText = contentAsString(result)
      bodyText must include("Select this stock")
    }
  }

  "application listStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.listStocks.apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username" -> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("Back")
    }
  }

  "application listCommonStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.listCommonStocks().apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username" -> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("Back")
    }
  }

  "application listCommonStocksTransformation" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.listCommonStocksTransformation().apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username" -> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("transformationbutton")
    }
  }

  "application transformData" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.transformData().apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username" -> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("Transformation in progress")
      bodyText must include("Please check the JOB_HISTORY table and AWS CMS to check the progress.")
    }
  }

  "application displayselectedStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubMessagesControllerComponents(),
        config = Configuration(ConfigFactory.load()).underlying)
      val result = controller.displayselectedStocks().apply(CSRFTokenHelper.addCSRFToken(FakeRequest().withSession("username" -> "root")))
      val bodyText = contentAsString(result)
      bodyText must include("Get Current Prices")
    }
  }
}
