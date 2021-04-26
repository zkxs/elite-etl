package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.michaelripley.elite_etl.dto.enums.{Allegiance, Government}

case class Faction(
  id: Int,
  name: String,
  @JsonProperty("allegiance_id") allegiance: Allegiance,
  @JsonProperty("home_system_id") homeSystem: java.lang.Integer,
  @JsonProperty("government_id") government: Government,
)
