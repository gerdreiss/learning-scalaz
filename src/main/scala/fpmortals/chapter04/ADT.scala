package fpmortals.chapter04

import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.{MaxSize, NonEmpty}
import eu.timepit.refined.numeric.Positive


object ADT extends App {

  type Name = NonEmpty And MaxSize[W.`10`.T]

  final case class Url()
  object Url {
    implicit def urlValidate: Validate.Plain[String, Url] =
      Validate.fromPartial(new java.net.URL(_), "Url", Url())
  }

  // import Url._

  final case class Person(
    name: String Refined Name,
    age: Int Refined Positive
    //homepage: String Refined Url
  )

  // this doesn't even compile
  // val p = Person(name = "", age = 0)
  // this does
  val p = Person(name = "John", age = 50/*, homepage = "http://github.com"*/)

  // This cannot compile - can't fool the lib/compiler ;-)
  // val n: String Refined NonEmpty = if (true) "John" else ""
  // val p1 = Person(name = n, age = 50)

  println(p)
}
