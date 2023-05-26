package ch.megard.pekko.http.cors

import java.util.Optional

private object OptionConverters {
  @inline final def toScala[A](o: Optional[A]): Option[A] = scala.compat.java8.OptionConverters.toScala(o)
  @inline final def toJava[A](o: Option[A]): Optional[A]  = scala.compat.java8.OptionConverters.toJava(o)
}
