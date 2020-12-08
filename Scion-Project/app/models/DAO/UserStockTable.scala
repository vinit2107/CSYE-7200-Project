package models.DAO

import scala.concurrent.Future

case class UserStock(username: String, stocks: String)

trait UserStockTable {
  //  this: DbConfiguration =>
  import DbConfiguration.config.profile.api._

  class UserStocks(tag: Tag) extends Table[UserStock](tag, "USERSTOCKS") {
    def username = column[String]("USERNAME", O.Length(24), O.PrimaryKey)
    def stockname = column[String]("STOCKS", O.Length(24))

    def * = (username, stockname) <> (UserStock.tupled, UserStock.unapply)
  }

  val userstocks = TableQuery[UserStocks]

  /* Function to check if the username persists in the database or not*/
  def filter_username(username: String): Future[Seq[UserStock]] = {
    DbConfiguration.config.db.run(userstocks.filter(_.username === username).result)
  }

  /*Function to insert record in User table*/
  def insert_stock_selection(userstock: UserStock): Future[Int] = {
    DbConfiguration.config.db.run(userstocks += userstock)
  }
}