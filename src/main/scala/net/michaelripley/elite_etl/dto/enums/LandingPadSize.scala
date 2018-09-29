package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class LandingPadSize(name: String)

object LandingPadSize {

  private val stateMap: Map[String, LandingPadSize] = {

    // list of all landing pad sizes
    val padSizes = List(
      "None",
      "S",
      "M",
      "L"
    )

    // build map
    padSizes
      .map(s => (s, new LandingPadSize(s)))
      .toMap
  }

  val values: Iterable[LandingPadSize] = stateMap.values

  def apply(name: String): LandingPadSize = {
    stateMap(name)
  }

  val deserializer: JsonDeserializer[LandingPadSize] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
