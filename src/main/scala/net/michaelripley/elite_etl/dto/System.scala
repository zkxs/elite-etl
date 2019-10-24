package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.michaelripley.elite_etl.dto.enums.Economy

case class System(
  id: Int,
  name: String,
  x: Double,
  y: Double,
  z: Double,
  @JsonProperty("minor_faction_presences") factionPresences: Iterable[FactionPresence],
  @JsonProperty("primary_economy") economy: Economy,
  states: Array[State]
) {

  def distance(system: System): Double = {
    val deltaX = x - system.x
    val deltaY = y - system.y
    val deltaZ = z - system.z
    Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
  }

}
