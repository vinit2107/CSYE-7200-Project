package controllers

import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

class FunctionTestSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory{
   "Web Function " must{
     "login in and access functions" in{
       go to s"http://localhost:9000/login?"
       //pageTitle mustBe "Login"
       //find("title").isEmpty mustBe false
       //find("title").foreach(e => e.text mustBe "Login")

       click on id("Username")
       textField(id("Username")).value = "BOBYXU"
       click on id("Password")
       textField(id("Password")).value = "123"
       submit()
//       eventually{
//        pageTitle mustBe "Action"
//         find(cssSelector("title")).isEmpty mustBe false
//         find(cssSelector("title")).foreach(e => e.text mustBe "Action")
//         findAll(cssSelector("p")).toList.map(_.text) mustBe List("Add Ticks","Remove Ticks","Historical Data Transformation","Get Current Prices")
//       }
     }
   }

}
