package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class State(name: String)

object State {

  private val stateMap: Map[String, State] = {

    // list of all possible states
    val states = List(
      "Boom",
      "Bust",
      "Civil Unrest",
      "Civil War",
      "Election",
      "Expansion",
      "Famine",
      "Investment",
      "Lockdown",
      "None",
      "Outbreak",
      "Retreat",
      "War"
    )

    // build map
    states
      .map(s => (s, new State(s)))
      .toMap
  }

  val values: Iterable[State] = stateMap.values

  def apply(name: String): State = {
    stateMap(name)
  }

  val deserializer: JsonDeserializer[State] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
