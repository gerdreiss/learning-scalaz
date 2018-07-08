package fpmortals.dronedynagents

import java.time.{Instant, ZonedDateTime}

import algebra._
import logic._
import scalaz._
import Scalaz._

object Data {
  val node1 = MachineNode("1243d1af-828f-4ba3-9fc0-a19d86852b5a")
  val node2 = MachineNode("550c4943-229e-47b0-b6be-3d686c5f013f")
  val managed = NonEmptyList(node1, node2)

  val time1: Instant = ZonedDateTime.parse("2017-03-03T18:07:00.000+01:00[Europe/London]").toInstant
  val time2: Instant = ZonedDateTime.parse("2017-03-03T18:59:00.000+01:00[Europe/London]").toInstant // +52 mins
  val time3: Instant = ZonedDateTime.parse("2017-03-03T19:06:00.000+01:00[Europe/London]").toInstant // +59 mins
  val time4: Instant = ZonedDateTime.parse("2017-03-03T23:07:00.000+01:00[Europe/London]").toInstant // +5 hours

  val needsAgents = WorldView(5, 0, managed, Map.empty, Map.empty, time1)
}

class Mutable(state: WorldView) {
  var started, stopped: Int = 0

  private val D: Drone[Id] = new Drone[Id] {
    def getBacklog: Int = state.backlog
    def getAgents: Int = state.agents
  }

  private val M: Machines[Id] = new Machines[Id] {
    def getAlive: Map[MachineNode, Instant] = state.alive
    def getManaged: NonEmptyList[MachineNode] = state.managed
    def getTime: Instant = state.time
    def start(node: MachineNode): MachineNode = {
      started += 1; node
    }
    def stop(node: MachineNode): MachineNode = {
      stopped += 1; node
    }
  }

  val program = new DynAgents[Id](D, M)
}
