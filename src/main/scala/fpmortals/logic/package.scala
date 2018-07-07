package fpmortals

import java.time.Instant
import java.time.temporal.ChronoUnit

import scala.concurrent.duration._
import scalaz._
import Scalaz._
import fpmortals.algebra._

import scala.language.higherKinds


package object logic {

  final case class WorldView(
    backlog: Int,
    agents: Int,
    managed: NonEmptyList[MachineNode],
    alive: Map[MachineNode, Instant],
    pending: Map[MachineNode, Instant],
    time: Instant
  )

  final class DynAgents[F[_]](D: Drone[F], M: Machines[F])(implicit F: Monad[F]) {
    def initial: F[WorldView] = for {
      db <- D.getBacklog
      da <- D.getAgents
      mm <- M.getManaged
      ma <- M.getAlive
      mt <- M.getTime
    } yield WorldView(db, da, mm, ma, Map.empty, mt)

    def update(old: WorldView): F[WorldView] = for {
      snap <- initial
      changed = symdiff(old.alive.keySet, snap.alive.keySet)
      pending = (old.pending -- changed) filterNot {
        case (_, started) => timediff(started, snap.time) >= 10.minutes
      }
      update = snap.copy(pending = pending)
    } yield update

    private def symdiff[T](a: Set[T], b: Set[T]): Set[T] =
      (a union b) -- (a intersect b)

    private def timediff(from: Instant, to: Instant): FiniteDuration =
      ChronoUnit.MINUTES.between(from, to).minutes


    private object NeedsAgent {
      def unapply(world: WorldView): Option[MachineNode] = world match {
        case WorldView(backlog, 0, managed, alive, pending, _)
          if backlog > 0 && alive.isEmpty && pending.isEmpty => Option(managed.head)
        case _ => None
      }
    }

    private object Stale {
      def unapply(world: WorldView): Option[NonEmptyList[MachineNode]] = world match {
        case WorldView(backlog, _, _, alive, pending, time) if alive.nonEmpty =>
          (alive -- pending.keys)
            .collect {
              case (node, started) if backlog == 0 && timediff(started, time).toMinutes % 60 >= 58 => node
              case (node, started) if timediff(started, time) >= 5.hours => node
            }
            .toList.toNel

        case _ => None
      }
    }

    def act(world: WorldView): F[WorldView] = world match {
      case NeedsAgent(node) =>
        for {
          _ <- M.start(node)
          update = world.copy(pending = Map(node -> world.time))
        } yield update

      case Stale(nodes) =>
        nodes.foldLeftM(world) { (world, node) =>
          for {
            _ <- M.stop(node)
            update = world.copy(pending = world.pending + (node -> world.time))
          } yield update
        }

      case _ => world.pure[F]
    }

  }

}
