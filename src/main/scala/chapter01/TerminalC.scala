package chapter01

import scala.concurrent.Future
import scala.language.higherKinds


object TerminalC {

  trait Terminal[C[_]] {
    def read: C[String]
    def write(t: String): C[Unit]
  }

  type Now[X] = X

  object TerminalSync extends Terminal[Now] {
    override def read: Now[String] = ???
    override def write(t: String): Now[Unit] = ???
  }

  object TerminalAsync extends Terminal[Future] {
    override def read: Future[String] = ???
    override def write(t: String): Future[Unit] = ???
  }


  trait Execution[C[_]] {
    def doAndThen[A, B](c: C[A])(f: A => C[B]): C[B]
    def create[B](b: B): C[B]
  }


  def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
    e.doAndThen(t.read) { in: String =>
      e.doAndThen(t.write(in)) { _: Unit =>
        e.create(in)
      }
    }
}