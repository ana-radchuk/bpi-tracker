import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.concurrent.duration._

object AkkaHttp {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    var req = HttpRequest(
      method = HttpMethods.GET,
      uri = "https://api.coinbase.com/v2/prices/BTC-USD/spot"
    )

    val route: Route = (path("bpi") & get) {
        sendRequest(req).foreach(println)
        complete(sendRequest(req))
    }

    def sendRequest(request: HttpRequest): Future[String] = {
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response =>
        response.entity.toStrict(2.seconds))
        entityFuture.map(entity => entity.data.utf8String)
    }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8080).bind(route)
    println("Server is listening on port 8080")

  }
}
