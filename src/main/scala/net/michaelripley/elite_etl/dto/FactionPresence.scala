package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty

case class FactionPresence(
  @JsonProperty("minor_faction_id") id: Int,
  @JsonProperty("active_states") states: Iterable[State]
) {
  override def toString: String = id.toString
}
