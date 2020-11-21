package login

import scala.collection.mutable.Map
import scala.util.Try

case class User(username: String, password: String, name: String, email: String, city: String)

object UserDAO {
  private val users: Map[String, User] = Map()

  def getUser(username: String): Option[User] = users.get(username)

  def addUser(username: String, password: String, name: String, email: String, city: String): Boolean = {
    if(users.contains(username)){
      false
    }
    else {
      val user = User(username, password, name, email, city)
      users.put(username, user)
      true
    }
  }

  def validateUser(username: String, password: String): Boolean = users.get(username) match{
    case Some(user) => user.password == password
    case None => false
  }
}
