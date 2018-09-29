package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class StationType(name: String)

object StationType {

  private val stateMap: Map[String, StationType] = {

    // list of all possible station types
    val stationTypes = List(
      "Orbis Starport",
      "Coriolis Starport",
      "Civilian Outpost",
      "Industrial Outpost",
      "Ocellus Starport",
      "Commercial Outpost",
      "Mining Outpost",
      "Scientific Outpost",
      "Military Outpost",
      "Asteroid Base",
      "Unknown Outpost",
      "Planetary Outpost",
      "Planetary Settlement",
      "Planetary Port",
      "Unknown Planetary",
      "Planetary Engineer Base",
      "Megaship"
    )

    // build map
    stationTypes
      .map(s => (s, new StationType(s)))
      .toMap
  }

  val values: Iterable[StationType] = stateMap.values

  def apply(name: String): StationType = {
    stateMap(name)
  }

  val deserializer: JsonDeserializer[StationType] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
