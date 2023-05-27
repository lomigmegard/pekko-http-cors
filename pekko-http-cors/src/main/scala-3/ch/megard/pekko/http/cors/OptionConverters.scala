package ch.megard.pekko.http.cors

import java.util.Optional

object OptionConverters {
  final inline def toScala[A](o: Optional[A]): Option[A] = scala.jdk.javaapi.OptionConverters.toScala(o)
  final inline def toJava[A](o: Option[A]): Optional[A]  = scala.jdk.javaapi.OptionConverters.toJava(o)
}
