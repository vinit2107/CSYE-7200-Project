package login

import models.DAO.{User, UserTable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class LoginHandler {
  this: UserTable =>

  def addUser(username: String, password: String, name: String, email: String, city: String): Boolean = {

    if(Await.result(filter_username(username), Duration.Inf).length == 1) {
      false
    }
    else {
      val user = User(username, password, name, email, city)
      Await.result(insert_user(user), Duration.Inf)
      true
    }
  }

  def validateUser(username: String, password: String): Boolean =
    Await.result(filter_username(username), Duration.Inf).headOption match {
      case Some(user) => user.password == password
      case None => false
    }
}
