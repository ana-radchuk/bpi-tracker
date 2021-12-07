import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import scala.concurrent.duration._

object AkkaHttp {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  var req = HttpRequest(
    method = HttpMethods.GET,
    uri = "https://api.coinbase.com/v2/prices/BTC-USD/spot"
  )

  val req2 = HttpRequest(
    method = HttpMethods.GET,
    uri = "https://api.coindesk.com/v1/bpi/currentprice.json"
  )

  def sendRequest(request: HttpRequest) = {
    Source
      .repeat(request)
      .throttle(1, 30.seconds)
      .mapAsync(1)(Http().singleRequest(_).flatMap(response =>
        response.entity.toStrict(2.seconds)).map(entity => entity.data.utf8String))
      .runForeach(println)
  }

  def main(args: Array[String]): Unit = {
    sendRequest(req)
    sendRequest(req2)

  }
}
