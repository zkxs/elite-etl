package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.michaelripley.elite_etl.dto.enums.{Allegiance, State}

case class Faction(
  id: Int,
  name: String,
  @JsonProperty("allegiance_id") allegiance: Allegiance,
  state: Option[State],
  @JsonProperty("home_system_id") homeSystem: java.lang.Integer
)
