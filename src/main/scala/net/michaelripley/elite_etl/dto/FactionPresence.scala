package net.michaelripley.elite_etl.dto

import com.fasterxml.jackson.annotation.JsonProperty

case class FactionPresence(@JsonProperty("minor_faction_id") id: Int) {
  override def toString: String = id.toString
}
