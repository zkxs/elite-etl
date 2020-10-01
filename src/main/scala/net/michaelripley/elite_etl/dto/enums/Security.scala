package net.michaelripley.elite_etl.dto.enums

import java.util.NoSuchElementException

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

class Security(val id: Int, val name: String) {

  override def toString: String = s"Security($name)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Security]

  override def equals(other: Any): Boolean = other match {
    case that: Security =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode
}

object Security {

  private val idMap: Map[Int, Security] = Map(
    64 -> new Security(1, "Anarchy"),
    16 -> new Security(16, "Low"),
    32 -> new Security(32, "Medium"),
    48 -> new Security(48, "High")
  )

  private val nameMap: Map[String, Security] = idMap.map(t => (t._2.name, t._2))

  val values: Iterable[Security] = idMap.values

  def apply(id: Int): Security = {
    try {
      idMap(id)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(id.toString)
    }
  }

  def apply(name: String): Security = {
    nameMap(name)
  }

  val deserializer: JsonDeserializer[Security] = (parser: JsonParser, _: DeserializationContext) => {
    Security(parser.getIntValue)
  }
}
