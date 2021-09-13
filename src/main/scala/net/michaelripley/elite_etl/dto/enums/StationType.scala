package net.michaelripley.elite_etl.dto.enums

import java.util.NoSuchElementException

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class StationType(name: String)

object StationType {

  private val typeMap: Map[String, StationType] = {

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
      "Megaship",
      "Non-Dockable Orbital",
      "Unknown Dockable",
      "Civilian Megaship",
      "Fleet Carrier",
      "Odyssey Settlement",
    )

    // build map
    stationTypes
      .map(s => (s, new StationType(s)))
      .toMap
  }

  val values: Iterable[StationType] = typeMap.values

  def apply(name: String): StationType = {
    try {
      typeMap(name)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(name)
    }
  }

  val deserializer: JsonDeserializer[StationType] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
