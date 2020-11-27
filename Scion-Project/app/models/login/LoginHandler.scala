package login

import javax.security.sasl.AuthenticationException
import models.DAO.{User, UserTable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class LoginHandler {
  this: UserTable =>

  def addUser(username: String, password: String, name: String, email: String, city: String): Try[Boolean] = {
    (Await.result(filter_username(username), Duration.Inf).length == 1) match {
      case true => Failure(new Exception("Username already exists!"))
      case false => {
        val user = User(username, password, name, email, city)
        Await.result(insert_user(user), Duration.Inf)
        Success(true)
      }
    }
  }


  def validateUser(username: String, password: String): Try[Boolean] =
    Await.result(filter_username(username), Duration.Inf).headOption match {
      case Some(user) => (user.password == password) match {
        case true => Success(true)
        case false => Failure(new AuthenticationException("Wrong password for the given username"))
      }
      case None => Failure(new Exception("Username does not exist."))
    }
}
