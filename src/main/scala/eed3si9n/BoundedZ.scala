package eed3si9n

import scalaz._
import Scalaz._

object BoundedZ extends App {

  implicit val doubleEnumInstance: Enum[Double] = new Enum[Double] {
    override def order(x: Double, y: Double): Ordering = Ordering.fromLessThan(x, y)(_ < _)
    override def succ(a: Double): Double = if (a == Double.MaxValue) Double.MinValue else a + 1L
    override def pred(a: Double): Double = if (a == Double.MinValue) Double.MaxValue else a - 1L
    override def min: Option[Double] = Double.MinValue.some
    override def max: Option[Double] = Double.MaxValue.some
  }

  println(implicitly[Enum[Char]].min)
  println(implicitly[Enum[Char]].max)
  println(implicitly[Enum[Int]].min)
  println(implicitly[Enum[Double]].max)
  println(implicitly[Enum[Double]].max.map(_.succ))

}
