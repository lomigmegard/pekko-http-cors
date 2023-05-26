package ch.megard.pekko.http.cors

import java.util.Optional

object OptionConverters {
  @inline final def toScala[A](o: Optional[A]): Option[A] = scala.jdk.javaapi.OptionConverters.toScala(o)
  @inline final def toJava[A](o: Option[A]): Optional[A]  = scala.jdk.javaapi.OptionConverters.toJava(o)
}
