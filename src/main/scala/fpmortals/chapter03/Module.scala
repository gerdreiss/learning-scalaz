package fpmortals.chapter03

import scala.concurrent.duration._
import scala.language.higherKinds

object Module {

  import Algebra._
  import scalaz._
  import Scalaz._

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
        (alive -- pending.keys).collect {
          case (n, started) if backlog == 0 && (time - started).toMinutes % 60 >= 58 => n
          case (n, started) if (time - started) >= 5.hours => n
        }.toList.toNel

      case _ => None
    }
  }

  final class DynAgentsModule[F[_] : Monad](D: Drone[F], M: Machines[F]) extends DynAgents[F] {
    override def initial: F[WorldView] =
      (D.getBacklog ⊛ D.getAgents ⊛ M.getManaged ⊛ M.getAlive ⊛ M.getTime) {
        case (db, da, mm, ma, mt) => WorldView(db, da, mm, ma, Map.empty, mt)
      }
    //for {
    //  db <- D.getBacklog
    //  da <- D.getAgents
    //  mm <- M.getManaged
    //  ma <- M.getAlive
    //  mt <- M.getTime
    //} yield WorldView(db, da, mm, ma, Map.empty, mt)

    override def update(old: WorldView): F[WorldView] = for {
      snapshot <- initial
      changed = symdiff(old.alive.keySet, snapshot.alive.keySet)
      pending = (old.pending -- changed).filterNot {
        case (_, started) => (snapshot.time - started) >= 10.minutes
      }
      update = snapshot.copy(pending = pending)
    } yield update

    private def symdiff[T](a: Set[T], b: Set[T]): Set[T] =
      (a union b) -- (a intersect b)

    override def act(world: WorldView): F[WorldView] = world match {
      case NeedsAgent(node) =>
        for {
          _ <- M.start(node)
          update = world.copy(pending = Map(node -> world.time))
        } yield update

      case Stale(nodes) =>
        nodes.foldLeftM(world) { (world, n) =>
          for {
            _ <- M.stop(n)
            update = world.copy(pending = world.pending + (n -> world.time))
          } yield update
        }

      case _ => world.pure[F]
    }

  }

}
