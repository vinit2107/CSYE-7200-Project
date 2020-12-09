package ClusterConfiguration

import akka.actor.typed.ActorSystem
import com.typesafe.config.Config


object ActorSystemCC {

  def createActorSystem(ticker: String)(implicit config: Config) = {
    val system = ActorSystem(submitJob(), "spark-job-actor-system")
    system ! ticker
  }

}
