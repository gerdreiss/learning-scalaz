package eed3si9n

import scalaz._
import Scalaz._

object EqualZ extends App {

  // do not use /== due to bad precedence
  println(1.some =/= 2.some)
}
