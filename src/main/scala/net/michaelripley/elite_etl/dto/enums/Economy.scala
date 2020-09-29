package net.michaelripley.elite_etl.dto.enums

import java.util.NoSuchElementException

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
      "Private Enterprise",
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
    try {
      economyMap(name)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(name)
    }
  }

  val deserializer: JsonDeserializer[Economy] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
