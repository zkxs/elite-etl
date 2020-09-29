package net.michaelripley.elite_etl.dto.enums

import java.util.NoSuchElementException

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

class Allegiance(val id: Int, val name: String) {

  override def toString: String = s"Allegiance($name)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Allegiance]

  override def equals(other: Any): Boolean = other match {
    case that: Allegiance =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode
}

object Allegiance {

  private val idMap: Map[Int, Allegiance] = Map(
    1 -> new Allegiance(1, "Alliance"),
    2 -> new Allegiance(2, "Empire"),
    3 -> new Allegiance(3, "Federation"),
    4 -> new Allegiance(4, "Independent"),
    5 -> new Allegiance(5, "None"),
    7 -> new Allegiance(7, "Pilots Federation")
  )

  private val nameMap: Map[String, Allegiance] = idMap.map(t => (t._2.name, t._2))

  val values: Iterable[Allegiance] = idMap.values

  def apply(id: Int): Allegiance = {
    try {
      idMap(id)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(id.toString)
    }
  }

  def apply(name: String): Allegiance = {
    nameMap(name)
  }

  val deserializer: JsonDeserializer[Allegiance] = (parser: JsonParser, _: DeserializationContext) => {
    Allegiance(parser.getIntValue)
  }
}
