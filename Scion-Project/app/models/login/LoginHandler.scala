package models.login

import models.DAO.{User, UserTable}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginHandler {
  this: UserTable =>

  def addUser(username: String, password: String, name: String, email: String, city: String): Future[Boolean] = {
    filter_username(username).map(s => s.length == 1).map(b => b match {
      case true => false
      case false => {
        val user = User(username, encryptPassword(password), name, email, city)
        insert_user(user)
        true
      }
    })
  }

  def validateUser(username: String, password: String): Future[Boolean] = {
    filter_username(username).map(s => s.headOption match {
      case Some(u) => checkPassword(password, u.password)
      case None => false
    })
    }

  def encryptPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  def checkPassword(plainText: String, hashed: String): Boolean = {
    BCrypt.checkpw(plainText, hashed)
  }
}
