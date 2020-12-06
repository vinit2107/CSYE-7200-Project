package models.DAO

import scala.concurrent.Future

case class UserStock(username: String, stockname: String)

trait UserStockTable {
  //  this: DbConfiguration =>
  import DbConfiguration.config.profile.api._

  class UserStocks(tag: Tag) extends Table[UserStock](tag, "USERSTOCK") {
    def username = column[String]("USERNAME", O.Length(24), O.PrimaryKey)
    def stockname = column[String]("STOCKNAME", O.Length(24))

    def * = (username, stockname) <> (UserStock.tupled, UserStock.unapply)
  }

  val userstocks = TableQuery[UserStocks]

  /* Function to check if the username persists in the database or not*/
  def filter_username(username: String): Future[Seq[UserStock]] = {
    DbConfiguration.config.db.run(userstocks.filter(_.username === username).result)
  }

  /*Function to insert record in User table*/
  def insert_user(userstock: UserStock): Future[Int] = {
    DbConfiguration.config.db.run(userstocks += userstock)
  }

  /*Function to connect User table and Stock table*/
//  def connect_table(userstock: UserStock, usertable: UserTable, stocktable: StockTable): Future[Int] = {
//    DbConfiguration.config.db.run()
//  }
}