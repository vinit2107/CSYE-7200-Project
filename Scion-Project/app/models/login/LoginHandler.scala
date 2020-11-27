package models.login

import models.DAO.{User, UserTable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginHandler {
  this: UserTable =>

  def addUser(username: String, password: String, name: String, email: String, city: String): Future[Boolean] = {
    filter_username(username).map(s => s.length == 1).map(b => b match {
      case true => false
      case false => {
        val user = User(username, password, name, email, city)
        insert_user(user)
        true
      }
    })
  }

  def validateUser(username: String, password: String): Future[Boolean] = {
    filter_username(username).map(s => s.headOption match {
      case Some(u) => u.password == password
      case None => false
    })
    }
}
