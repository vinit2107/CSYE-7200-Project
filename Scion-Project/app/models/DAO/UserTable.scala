package models.DAO

import scala.concurrent.Future

case class User(username: String, password: String, name: String, email: String, city: String)

trait UserTable {
//  this: DbConfiguration =>
  import DbConfiguration.config.profile.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def username = column[String]("USERNAME", O.Length(24), O.PrimaryKey)
    def password = column[String]("PASSWORD", O.Length(24))
    def name = column[String]("NAME")
    def email = column[String]("EMAIL", O.Length(50))
    def city = column[String]("CITY")

    def * = (username, password, name, email, city) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

  /* Function to check if the username persists in the database or not*/
  def filter_username(username: String): Future[Seq[User]] = {
    DbConfiguration.config.db.run(users.filter(_.username === username).result)
  }

  /*Function to insert record in User table*/
  def insert_user(user: User): Future[Int] = {
    DbConfiguration.config.db.run(users += user)
  }
}