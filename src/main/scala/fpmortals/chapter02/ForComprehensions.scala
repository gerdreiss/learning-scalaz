package fpmortals.chapter02

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits._
import scalaz._
import Scalaz._

import scala.concurrent.duration.Duration

object ForComprehensions extends App {

  def liftFutureOption[A](f: Future[Option[A]])       = OptionT(f)
  def liftFuture[A](f: Future[A]): OptionT[Future, A] = f.liftM[OptionT]
  def liftOption[A](o: Option[A]): OptionT[Future, A] = OptionT(o.pure[Future])
  def lift[A](a: A): OptionT[Future, A]               = liftOption(Option(a))

  val resultOptionT: OptionT[Future, Int] = for {
    a <- Future(Some(1)) |> liftFutureOption
    b <- Future(Some(2)) |> liftFutureOption
    c <- Future(3)       |> liftFuture
    d <- Some(4  )       |> liftOption
    e <- 10              |> lift
  } yield e * (a * b) / (c * d)

  val resultFutureOption = resultOptionT.run
  val resultOption = Await.result(resultFutureOption, Duration.Inf)

  resultOption foreach println
}
