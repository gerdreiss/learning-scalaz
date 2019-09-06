package fpmortals.chapter01

import scala.concurrent.Future
import scala.language.higherKinds

object Terminal extends App {

  type Now[X] = X

  // The Terminal ADT
  trait Terminal[C[_]] {
    def read: C[String]
    def write(t: String): C[Unit]
  }
  object TerminalSync extends Terminal[Now] {
    def read: String = ???
    def write(t: String): Unit = ???
  }
  object TerminalAsync extends Terminal[Future] {
    def read: Future[String] = ???
    def write(t: String): Future[Unit] = ???
  }
  // The Exception ADT
  trait Execution[C[_]] {
    def chain[A, B](c: C[A])(f: A => C[B]): C[B]
    def create[B](b: B): C[B]
  }
  object Execution {
    implicit class Ops[A, C[_]](c: C[A]) {
      def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] = e.chain(c)(f)
      def map[B](f: A => B)(implicit e: Execution[C]): C[B] = e.chain(c)(f andThen e.create)
    }
  }
  // Lazy execution context -> IO
  final class IO[A](val interpret: () => A) {
    def map[B](f: A => B): IO[B] = IO(f(interpret()))
    def flatMap[B](f: A => IO[B]): IO[B] = IO(f(interpret()).interpret())
  }
  object IO {
    def apply[A](a: =>A): IO[A] = new IO(() => a)
  }
  object TerminalIO extends Terminal[IO] {
    def read: IO[String]           = IO { io.StdIn.readLine }
    def write(t: String): IO[Unit] = IO { println(t) }
  }
  object ExecutionIO extends Execution[IO] {
    override def chain[A, B](c: IO[A])(f: A => IO[B]): IO[B] = c.flatMap(f)
    override def create[B](b: B): IO[B] = IO(b)
  }

  import Execution._
  // naive impl
  //def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
  //  e.chain(t.read) { in: String =>
  //    e.chain(t.write(in)) { _: Unit =>
  //      e.create(in)
  //    }
  //  }
  // more functional impl
  //def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
  //  t.read.flatMap { in: String =>
  //    t.write(in).map { _: Unit =>
  //      in
  //    }
  //  }
  // impl with for comprehension
  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _  <- t.write(in)
    } yield in

  implicit val t: Terminal[IO] = TerminalIO
  implicit val e: Execution[IO] = ExecutionIO

  //val futureEcho: Future[String] = echo[Future]
  val delayed: IO[String] = echo[IO]
  delayed.interpret()
}
