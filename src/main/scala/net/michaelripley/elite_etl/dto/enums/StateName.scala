package net.michaelripley.elite_etl.dto.enums

import java.util.NoSuchElementException

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class StateName(name: String)

object StateName {

  private val stateMap: Map[String, StateName] = {

    // list of all possible states
    val states = List(
      "Blight",
      "Boom",
      "Bust",
      "Civil Liberty",
      "Civil Unrest",
      "Civil War",
      "Damaged",
      "Election",
      "Expansion",
      "Famine",
      "Investment",
      "Lockdown",
      "None",
      "Outbreak",
      "Pirate Attack",
      "Retreat",
      "Under Repairs",
      "War"
    )

    // build map
    states
      .map(s => (s, new StateName(s)))
      .toMap
  }

  val values: Iterable[StateName] = stateMap.values

  def apply(name: String): StateName = {
    try {
      stateMap(name)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(name);
    }
  }

  val deserializer: JsonDeserializer[StateName] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
