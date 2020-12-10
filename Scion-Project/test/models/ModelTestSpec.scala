package models

import models.DAO.{StocksTable, UserStockTable, UserTable}
import models.StockSelection.{ListStocks, StockSelectionHandler}
import models.login.LoginHandler
import org.scalatestplus.play._

class ModelTestSpec extends PlaySpec{
  val loginHandler = new LoginHandler() with UserTable
  val liststocks = new ListStocks() with StocksTable
  val stockSelectionHandler = new StockSelectionHandler with UserStockTable
  "ModelTest" must{
    "valid login correct" in{
      loginHandler.validateUser("BOBYXU","123") mustBe(true)
    }

    "reject login with wrong username" in{
      loginHandler.validateUser("BOBYXU1","123") mustBe(false)
    }

    "reject login with wrong password" in{
      loginHandler.validateUser("BOBYXU","1234") mustBe(false)
    }

    "valid create new user and login in" in{
      loginHandler.addUser("BOBYX","1234","boby","123@123.com","Boston") mustBe(true)
      loginHandler.validateUser("BOBYX","123") mustBe(true)
    }

    "valid to list all stocks" in{
      liststocks.listAllStocks() mustBe(true)
    }

    "valid to fetch current stocks" in{
      stockSelectionHandler.fetch_current_stocks("APPLE") mustBe(true)
    }

    "valid to store new stocks" in{
      stockSelectionHandler.store_new_stocks("BOBYXU","MSI") mustBe(true)
    }

    "valid to delete stocks" in{
      stockSelectionHandler.delete_new_stocks("BOBYXU","MSI") mustBe(true)
    }
  }
}
