package models.StockSelection

import models.DAO.{UserStockTable, UserStock}

import scala.concurrent.ExecutionContext.Implicits.global

class StockSelectionHandler {
  this: UserStockTable =>

  def fetch_current_stocks(username: String) = {
    filter_username(username).map(seq => seq match {
      case Nil => Seq[String]()
      case x => x.head.stocks.split(",").toSeq
    })
  }

  def store_new_stocks(userStock: UserStock) = {
    insert_stock_selection(userStock)
  }

}
