package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.michaelripley.elite_etl.dto.enums.{Economy, LandingPadSize, State, StationType}

case class Station(
  id: Int,
  name: String,
  @JsonProperty("distance_to_star") arrivalDistance: java.lang.Integer,
  state: Option[State],
  @JsonProperty("system_id") systemId: Int,
  economies: Array[Economy],
  @JsonProperty("type") stationType: StationType,
  @JsonProperty("max_landing_pad_size") maxLandingPadSize: LandingPadSize,
  @JsonProperty("has_docking") hasDocking: Boolean,
  @JsonProperty("is_planetary") isPlanetary: Boolean
)
