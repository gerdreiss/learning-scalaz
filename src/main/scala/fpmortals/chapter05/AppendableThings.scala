package fpmortals.chapter05

object AppendableThings extends App {

  import scalaz._
  import Scalaz._
  import java.time.{LocalDate => LD}

  sealed abstract class Currency
  case object EUR extends Currency
  case object USD extends Currency
  case object CHF extends Currency

  final case class TradeTemplate(
    payments: List[LD],
    ccy: Option[Currency],
    otc: Option[Boolean]
  )

  object TradeTemplate {
    // Orphan instances such as lastWins are the easiest way to break coherence.
    // Please don’t break typeclass coherence at home, kids.
    implicit def lastWins[A]: Monoid[Option[A]] = Monoid.instance(
      {
        case (None, None) => None
        case (only, None) => only
        case (None, only) => only
        case (_, winner) => winner
      },
      None
    )
    implicit val monoid: Monoid[TradeTemplate] = Monoid.instance(
      (a, b) => TradeTemplate(
        a.payments |+| b.payments,
        a.ccy |+| b.ccy,
        a.otc |+| b.otc
      ),
      TradeTemplate(Nil, None, None)
    )
  }


  def takeTemplates(templates: List[TradeTemplate]) = {
    val zero = Monoid[TradeTemplate].zero
    templates.foldLeft(zero)(_ |+| _)
  }


  val templates = List(
    TradeTemplate(Nil, None, None),
    TradeTemplate(Nil, Some(EUR), None),
    TradeTemplate(List(LD.of(2017, 8, 5)), Some(USD), None),
    TradeTemplate(List(LD.of(2017, 9, 5)), None, Some(true)),
    TradeTemplate(Nil, None, Some(false))
  )

  println(takeTemplates(templates))
}
