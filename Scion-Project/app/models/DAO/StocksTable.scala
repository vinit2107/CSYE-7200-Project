package models.DAO

import scala.concurrent.Future

case class Stock(stockname: String, shortname: String)

trait StocksTable {
  //  this: DbConfiguration =>
  import DbConfiguration.config.profile.api._

  class Stocks(tag: Tag) extends Table[Stock](tag, "STOCKS") {
    def stockname = column[String]("STOCKNAME", O.Length(24), O.PrimaryKey)
    def shortname = column[String]("SHORTNAME",O.Length(24))

    def * = (stockname, shortname) <> (Stock.tupled, Stock.unapply)
  }

  val stocks = TableQuery[Stocks]

  /* Function to check if the stockname persists in the database or not*/
  def filter_stockname(stockname: String): Future[Seq[Stock]] = {
    DbConfiguration.config.db.run(stocks.filter(_.stockname === stockname).result)
  }

  /* Function to list all the stocks*/
  def get_stocklist(): Future[Seq[Stock]] = {
    DbConfiguration.config.db.run(stocks.result)
  }

  /*Function to insert record in Stocks table*/
  def insert_stock(stock: Stock): Future[Int] = {
    DbConfiguration.config.db.run(stocks += stock)
  }
}