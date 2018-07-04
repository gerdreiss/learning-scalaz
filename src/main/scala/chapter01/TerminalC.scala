package chapter01

import scala.concurrent.Future
import scala.language.higherKinds


object TerminalC {

  // TERMINAL

  trait Terminal[C[_]] {
    def read: C[String]
    def write(t: String): C[Unit]
  }

  type Now[X] = X

  object TerminalSync extends Terminal[Now] {
    override def read: Now[String] = "Sync read"
    override def write(t: String): Now[Unit] = "Sync write"
  }

  object TerminalAsync extends Terminal[Future] {
    override def read: Future[String] = Future { "Async read" }
    override def write(t: String): Future[Unit] = Future { "Async write" }
  }

  // EXECUTION

  trait Execution[C[_]] {
    def doAndThen[A, B](c: C[A])(f: A => C[B]): C[B]
    def create[B](b: B): C[B]
  }


  // this
  // def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
  //   e.doAndThen(t.read) { in: String =>
  //     e.doAndThen(t.write(in)) { _: Unit =>
  //       e.create(in)
  //     }
  //   }
  //
  // can be refactored to this, to enable a for comprehension over the execution
  object Execution {
    implicit class Ops[A, C[_]](c: C[A]) {
      def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] = e.doAndThen(c)(f)
      def map[B](f: A => B)(implicit e: Execution[C]): C[B] = flatMap(f andThen e.create)
    }
  }


  // here we use the implicit flatMap and map functions
  //def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
  //  t.read.flatMap { in: String =>
  //    t.write(in).map { _: Unit =>
  //      in
  //    }
  //  }
  //
  // which can be rewritten into a for comprehension, or a monadic calculation
  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _  <- t.write(in)
    } yield in

}
