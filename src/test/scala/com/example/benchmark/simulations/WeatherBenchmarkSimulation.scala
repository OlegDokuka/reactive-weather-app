package com.example.benchmark.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class Location(var country: String, var city: String)

class WeatherBenchmarkSimulation extends Simulation {

  val baseURL = Option(System.getProperty("baseURL")) getOrElse
    (Option(System.getenv("BASE_URL")) getOrElse """http://localhost:8080""")

  val httpConf = http
    .baseURL(s"$baseURL/api/weather") // Here is the root for all relative URLs
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .header("Connection", "keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

  val seq = Map(
    "Belgium" -> "Brussels",
    "USA" -> "Las Vegas"
  ).map { case (k, v) => new Location(k, v) }.toSeq

  val scn = scenario("Weather App")
    .repeat(1000) {
      foreach(_ => seq, "location") {
        exec(http("Get Weather Page")
          .get("/now/${location.country}/${location.city}")
          .check(status.is(200))).exitHereIfFailed
          .pause(1 millisecond)
      }
    }

  setUp(scn.inject(atOnceUsers(100)).protocols(httpConf))
}