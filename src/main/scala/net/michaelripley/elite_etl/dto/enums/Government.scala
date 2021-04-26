package net.michaelripley.elite_etl.dto.enums

import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

import java.util.NoSuchElementException

class Government(val id: Int, val name: String) {

  override def toString: String = s"Government($name)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Government]

  override def equals(other: Any): Boolean = other match {
    case that: Government =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode
}

object Government {

  private val idMap: Map[Int, Government] = Map(
     16 -> new Government( 16, "Anarchy"),
     32 -> new Government( 32, "Communism"),
     48 -> new Government( 48, "Confederacy"),
     64 -> new Government( 64, "Corporate"),
     80 -> new Government( 80, "Cooperative"),
     96 -> new Government( 96, "Democracy"),
    112 -> new Government(112, "Dictatorship"),
    128 -> new Government(128, "Feudal"),
    144 -> new Government(144, "Patronage"),
    150 -> new Government(150, "Prison Colony"),
    160 -> new Government(160, "Theocracy"),
    176 -> new Government(176, "None"),
    192 -> new Government(192, "Engineer"),
    208 -> new Government(208, "Prison"),
  )

  private val nameMap: Map[String, Government] = idMap.map(t => (t._2.name, t._2))

  val values: Iterable[Government] = idMap.values

  def apply(id: Int): Government = {
    try {
      idMap(id)
    } catch {
      case _: NoSuchElementException => throw new NoSuchElementException(id.toString)
    }
  }

  def apply(name: String): Government = {
    nameMap(name)
  }

  val deserializer: JsonDeserializer[Government] = (parser: JsonParser, _: DeserializationContext) => {
    Government(parser.getIntValue)
  }
}
