package models.StockSelection

import models.DAO.StocksTable

import scala.concurrent.ExecutionContext.Implicits.global

class ListStocks {
  this: StocksTable =>

    def listAllStocks() = {
      get_stocklist().map(s => s match {
        case Nil => Seq[String]()
        case _ => s.map(stock => stock.stockname)
      })
    }
}
