package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class Economy(name: String)

object Economy {

  private val economyMap: Map[String, Economy] = {

    // list of all possible economies
    val economies = List(
      "Agriculture",
      "Colony",
      "Damaged",
      "Extraction",
      "High Tech",
      "Industrial",
      "Military",
      "None",
      "Prison",
      "Refinery",
      "Repair",
      "Rescue",
      "Service",
      "Terraforming",
      "Tourism"
    )

    // build map
    economies
      .map(s => (s, new Economy(s)))
      .toMap
  }

  val values: Iterable[Economy] = economyMap.values

  def apply(name: String): Economy = {
    economyMap(name)
  }

  val deserializer: JsonDeserializer[Economy] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
