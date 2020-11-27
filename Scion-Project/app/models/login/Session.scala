//package login
//
//import java.time.LocalDateTime
//import java.util.UUID
//import scala.collection.mutable.Map
//
//case class Session(token: String, username: String, expirationTime: LocalDateTime)
//
//object Session{
//  val sessions: Map[String, Session] = Map()
//
//  def getSession(token: String): Option[Session] = sessions.get(token)
//
//  def generateToken(username: String): String = {
//    val token = s"$username-token-${UUID.randomUUID().toString}"
//    sessions.put(token, Session(token, username, LocalDateTime.now()))
//    token
//  }
//}