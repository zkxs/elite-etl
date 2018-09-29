package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.michaelripley.elite_etl.dto.enums.Allegiance

case class Faction(
  id: Int,
  name: String,
  @JsonProperty("allegiance_id") allegiance: Allegiance
)
