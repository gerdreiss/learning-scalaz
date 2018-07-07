package fpmortals.chapter04

object BetterUseOfImplicitClasses extends App {

  // implicit class is syntax sugar for an implicit conversion:
  // implicit def DoubleOps(x: Double): DoubleOps = new DoubleOps(x)
  // class DoubleOps(x: Double) {
  //   def sin: Double = java.lang.Math.sin(x)
  // }
  // Which unfortunately has a runtime cost: each time the extension method is called,
  // an intermediate DoubleOps will be constructed and then thrown away.
  // This can contribute to GC pressure in hotspots.
  // There is a slightly more verbose form of implicit class
  // that avoids the allocation and is therefore preferred:
  implicit final class DoubleOps(val x: Double) extends AnyVal {
    def sin: Double = java.lang.Math.sin(x)
  }

  println(1.0.sin)
}
