package ch.megard.pekko.http.cors

import java.util.concurrent.TimeUnit

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.apache.pekko.http.scaladsl.model.headers.{Origin, `Access-Control-Request-Method`}
import org.apache.pekko.http.scaladsl.model.{HttpMethods, HttpRequest}
import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import ch.megard.pekko.http.cors.scaladsl.CorsDirectives
import ch.megard.pekko.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.config.ConfigFactory
import org.openjdk.jmh.annotations._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class CorsBenchmark extends Directives with CorsDirectives {
  private val config = ConfigFactory.parseString("pekko.loglevel = ERROR").withFallback(ConfigFactory.load())

  implicit private val system: ActorSystem  = ActorSystem("CorsBenchmark", config)
  implicit private val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val http         = Http()
  private val corsSettings = CorsSettings.default

  private var binding: ServerBinding        = _
  private var request: HttpRequest          = _
  private var requestCors: HttpRequest      = _
  private var requestPreflight: HttpRequest = _

  @Setup
  def setup(): Unit = {
    val route = {
      path("baseline") {
        get {
          complete("ok")
        }
      } ~ path("cors") {
        cors(corsSettings) {
          get {
            complete("ok")
          }
        }
      }
    }
    val origin = Origin("http://example.com")

    binding = Await.result(http.newServerAt("127.0.0.1", 0).bind(route), 1.second)
    val base = s"http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}"

    request = HttpRequest(uri = base + "/baseline")
    requestCors = HttpRequest(
      method = HttpMethods.GET,
      uri = base + "/cors",
      headers = List(origin)
    )
    requestPreflight = HttpRequest(
      method = HttpMethods.OPTIONS,
      uri = base + "/cors",
      headers = List(origin, `Access-Control-Request-Method`(HttpMethods.GET))
    )
  }

  @TearDown
  def shutdown(): Unit = {
    val f = for {
      _ <- http.shutdownAllConnectionPools()
      _ <- binding.terminate(1.second)
      _ <- system.terminate()
    } yield ()
    Await.ready(f, 5.seconds)
  }

  @Benchmark
  def baseline(): Unit = {
    val f = http.singleRequest(request).flatMap(r => Unmarshal(r.entity).to[String])
    assert(Await.result(f, 1.second) == "ok")
  }

  @Benchmark
  def default_cors(): Unit = {
    val f = http.singleRequest(requestCors).flatMap(r => Unmarshal(r.entity).to[String])
    assert(Await.result(f, 1.second) == "ok")
  }

  @Benchmark
  def default_preflight(): Unit = {
    val f = http.singleRequest(requestPreflight).flatMap(r => Unmarshal(r.entity).to[String])
    assert(Await.result(f, 1.second) == "")
  }
}
