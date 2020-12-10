package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.WebSocketRequest
import akka.stream.ActorMaterializer
import com.typesafe.config.Config

object CurrentPriceWebSocket {

  implicit val actorSystem = ActorSystem()

  def connect(ticker: String)(implicit config: Config) = {

  }

  def createWebSocket(config: Config) = {
    val request = WebSocketRequest(uri = config.getString(""))
    val webSocketFlow = Http().webSocketClientFlow(request)
    webSocketFlow
  }

}
