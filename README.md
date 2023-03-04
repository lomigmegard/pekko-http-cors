# Pekko Http Cors

[![Software License](https://img.shields.io/badge/license-Apache%202-brightgreen.svg?style=flat)](LICENSE)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

CORS (Cross Origin Resource Sharing) is a mechanism to enable cross origin requests.

This is a Scala/Java implementation for the server-side targeting the [pekko-http](https://github.com/apache/incubator-pekko-http) library.

Pekko Http Cors is a fork of [Akka Http Cors](https://github.com/lomigmegard/akka-http-cors) 1.2.x release.

## Versions

At the moment only snapshots version are available, waiting for the first stable release of the [Apache Pekko](https://pekko.apache.org) project.

## Getting Pekko Http Cors
pekko-http-cors is deployed to Maven Central. Add it to your `build.sbt` or `Build.scala`:
```scala
libraryDependencies += "ch.megard" %% "pekko-http-cors" % "0.0.0-SNAPSHOT"
```

## Quick Start
The simplest way to enable CORS in your application is to use the `cors` directive.
Settings are passed as a parameter to the directive, with your overrides loaded from the `application.conf`.

```scala
import ch.megard.pekko.http.cors.scaladsl.CorsDirectives._

val route: Route = cors() {
  complete(...)
}
```

The settings can be updated programmatically too.
```scala
val settings = CorsSettings(...).withAllowGenericHttpRequests(false)
val strictRoute: Route = cors(settings) {
  complete(...)
}
```

A [full example](pekko-http-cors-example/src/main/scala/ch/megard/pekko/http/cors/scaladsl/CorsServer.scala), with proper exception and rejection handling, is available in the `pekko-http-cors-example` sub-project. 

## Rejection
The CORS directives can reject requests using the `CorsRejection` class. Requests can be either malformed or not allowed to access the resource.

A rejection handler is provided by the library to return meaningful HTTP responses. Read the pekko documentation (link TODO) to learn more about rejections, or if you need to write your own handler.
```scala
import org.apache.pekko.http.scaladsl.server.directives.ExecutionDirectives._
import ch.megard.pekko.http.cors.scaladsl.CorsDirectives._

val route: Route = handleRejections(corsRejectionHandler) {
  cors() {
    complete(...)
  }
}
```

## Java support

Java is supported, mirroring the Scala API. For usage, look at the full [Java CorsServer example](pekko-http-cors-example/src/main/java/ch/megard/pekko/http/cors/javadsl/CorsServer.java).

## Configuration

[Reference configuration](pekko-http-cors/src/main/resources/reference.conf).

#### allowGenericHttpRequests
`Boolean` with default value `true`.

If `true`, allow generic requests (that are outside the scope of the specification) to pass through the directive. Else, strict CORS filtering is applied and any invalid request will be rejected.

#### allowCredentials
`Boolean` with default value `true`.

Indicates whether the resource supports user credentials.  If `true`, the header `Access-Control-Allow-Credentials` is set in the response, indicating the actual request can include user credentials.

Examples of user credentials are: cookies, HTTP authentication or client-side certificates.

#### allowedOrigins
`HttpOriginMatcher` with default value `HttpOriginMatcher.*`.

List of origins that the CORS filter must allow. Can also be set to `*` to allow access to the resource from any origin. Controls the content of the `Access-Control-Allow-Origin` response header:
* if parameter is `*` **and** credentials are not allowed, a `*` is set in `Access-Control-Allow-Origin`.
* otherwise, the origins given in the `Origin` request header are echoed.

Hostname starting with `*.` will match any sub-domain. The scheme and the port are always strictly matched.

The actual or preflight request is rejected if any of the origins from the request is not allowed.

#### allowedHeaders
`HttpHeaderRange` with default value `HttpHeaderRange.*`.

 List of request headers that can be used when making an actual request. Controls the content of the `Access-Control-Allow-Headers` header in a preflight response:
 * if parameter is `*`, the headers from `Access-Control-Request-Headers` are echoed.
 * otherwise the parameter list is returned as part of the header.

#### allowedMethods
`Seq[HttpMethod]` with default value `Seq(GET, POST, HEAD, OPTIONS)`.

List of methods that can be used when making an actual request. The list is returned as part of the `Access-Control-Allow-Methods` preflight response header.

The preflight request will be rejected if the `Access-Control-Request-Method` header's method is not part of the list.

#### exposedHeaders
`Seq[String]` with default value `Seq.empty`.

List of headers (other than [simple response headers](https://www.w3.org/TR/cors/#simple-response-header)) that browsers are allowed to access. If not empty, this list is returned as part of the `Access-Control-Expose-Headers` header in the actual response.

#### maxAge
`Option[Long]` (in seconds) with default value `Some (30 * 60)`.

When set, the amount of seconds the browser is allowed to cache the results of a preflight request. This value is returned as part of the `Access-Control-Max-Age` preflight response header. If `None`, the header is not added to the preflight response.

## Benchmarks

Please look at the original project [Akka Http Cors](https://github.com/lomigmegard/akka-http-cors) for the existing benchmarks.

## References
- [W3C Specification: CORS](https://www.w3.org/TR/cors/)
- [RFC-6454: The Web Origin Concept](https://tools.ietf.org/html/rfc6454)

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).
