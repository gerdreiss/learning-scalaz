package fpmortals.chapter03

import scala.concurrent.duration._
import scala.language.higherKinds

object Algebra {

  import scalaz._

  final case class MachineNode(id: String)

  final case class WorldView(
    backlog: Int,
    agents: Int,
    managed: NonEmptyList[MachineNode],
    alive  : Map[MachineNode, Epoch],
    pending: Map[MachineNode, Epoch],
    time   : Epoch
  )

  final case class Epoch(millis: Long) extends AnyVal {
    def +(d: FiniteDuration): Epoch = Epoch(millis + d.toMillis)
    def -(e: Epoch): FiniteDuration = (millis - e.millis).millis
  }

  trait Drone[F[_]] {
    def getBacklog: F[Int]
    def getAgents: F[Int]
  }

  trait Machines[F[_]] {
    def getTime: F[Epoch]
    def getManaged: F[NonEmptyList[MachineNode]]
    def getAlive: F[Map[MachineNode, Epoch]]
    def start(node: MachineNode): F[MachineNode]
    def stop(node: MachineNode): F[MachineNode]
  }

  trait DynAgents[F[_]] {
    def initial: F[WorldView]
    def update(old: WorldView): F[WorldView]
    def act(world: WorldView): F[WorldView]
  }

}
