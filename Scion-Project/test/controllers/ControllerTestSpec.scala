package controllers

import org.scalatestplus.play._
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, stubControllerComponents}

class ControllerTestSpec extends PlaySpec {
  "application index" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.index.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Welcome")
      bodyText must include("TRADE WHAT YOU SEE,")
      bodyText must include("NOT WHAT YOU THINK!")
    }
  }

  "application login" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.login.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Your username")
      bodyText must include("Your password")
    }
  }

  "application signup" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.signup.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("First Name & Last Name")
    }
  }

  "application action" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.action.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Add Ticks")
      bodyText must include("Remove Ticks")
      bodyText must include("Historical Data Transformation")
    }
  }

  "application addRemoveStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.addRemoveStocks.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Select this stock")
    }
  }

  "application listStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.listStocks.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Back")
    }
  }

  "application listCommonStocks" must{
    "give back expected page" in {
      val controller = new HomeController(Helpers.stubControllerComponents()
      val result = controller.listCommonStocks().apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText must include("Back")
    }
  }
}
