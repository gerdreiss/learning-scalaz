package eed3si9n

import scalaz._
import Scalaz._

object EnumZ extends App {

  println('a' to 'z')
  println('a' |-> 'e')
  println('B'.succ)
  println((3 |=> 5).toList)

}
