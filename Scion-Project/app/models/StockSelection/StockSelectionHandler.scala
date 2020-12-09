package models.StockSelection

import models.DAO.{UserStockTable, UserStock}

import scala.concurrent.ExecutionContext.Implicits.global

class StockSelectionHandler {
  this: UserStockTable =>

  def fetch_current_stocks(username: String) = {
    filter_username(username).map(seq => seq match {
      case Nil => Seq[String]()
      case x => x.map(s => s.stocks)
    })
  }

  def store_new_stocks(username: String, stock: String) = {
    val userStock = UserStock(username, stock)
    insert_stock_selection(userStock)
  }
  def delete_new_stocks(username: String, stock: String) = {
    val userStock = UserStock(username, stock)
    remove_stock_selection(userStock)
  }

}
