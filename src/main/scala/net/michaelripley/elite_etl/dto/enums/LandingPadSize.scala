package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

case class LandingPadSize(name: String)

object LandingPadSize {

  private val padSizeMap: Map[String, LandingPadSize] = {

    // list of all landing pad sizes
    val padSizes = Map(
      "" -> "None",
      "None" -> "None",
      "S" -> "S",
      "M" -> "M",
      "L" -> "L"
    )

    // build map
    padSizes.map{case (k, v) => (k, new LandingPadSize(v))}
  }

  val values: Iterable[LandingPadSize] = padSizeMap.values

  def apply(name: String): LandingPadSize = {
    padSizeMap(name)
  }

  val deserializer: JsonDeserializer[LandingPadSize] = (parser: JsonParser, _: DeserializationContext) => {
    apply(parser.getValueAsString)
  }

}
