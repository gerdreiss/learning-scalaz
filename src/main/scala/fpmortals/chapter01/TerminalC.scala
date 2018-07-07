package fpmortals.chapter01

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.higherKinds

object TerminalC extends App {

  trait Terminal[C[_]] {
    def read: C[String]
    def write(t: String): C[Unit]
  }

  trait Execution[C[_]] {
    def doAndThen[A, B](c: C[A])(f: A => C[B]): C[B]
    def create[B](b: B): C[B]
  }


  // this function...
  // def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
  //   e.doAndThen(t.read) { in: String =>
  //     e.doAndThen(t.write(in)) { _: Unit =>
  //       e.create(in)
  //     }
  //   }
  //
  // ...can be refactored to this,
  // to enable a for comprehension over the execution
  object Execution {
    implicit class Ops[A, C[_]](c: C[A]) {
      def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] = e.doAndThen(c)(f)
      def map[B](f: A => B)(implicit e: Execution[C]): C[B] = flatMap(f andThen e.create)
    }
  }


  // here we use the implicit flatMap and map functions...
  //def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
  //  t.read.flatMap { in: String =>
  //    t.write(in).map { _: Unit =>
  //      in
  //    }
  //  }
  //
  // ...which can be rewritten into a for comprehension, or a monadic calculation
  import Execution._
  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _  <- t.write(in)
    } yield in


  // Testing

  // Synchronous
  //type Now[X] = X
  //implicit val terminalS: Terminal[Now] = new Terminal[Now] {
  //  override def read: Now[String] = "read something synchronously"
  //  override def write(t: String): Now[Unit] = println(t)
  //}
  //implicit val executionS: Execution[Now] = new Execution[Now] {
  //  override def doAndThen[A, B](c: Now[A])(f: A => Now[B]): Now[B] = f(c)
  //  override def create[B](b: B): Now[B] = b
  //}
  //echo

  // Asynchronous
  implicit val terminalA: Terminal[Future] = new Terminal[Future] {
    override def read: Future[String] = Future("read something asynchronously")
    override def write(t: String): Future[Unit] = Future(println(t))
  }
  implicit val executionA: Execution[Future] = new Execution[Future] {
    override def doAndThen[A, B](c: Future[A])(f: A => Future[B]): Future[B] = c flatMap f
    override def create[B](b: B): Future[B] = Future(b)
  }

  Await.result(echo, Duration.Inf)

}
