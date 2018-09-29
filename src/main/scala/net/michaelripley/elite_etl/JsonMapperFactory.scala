package net.michaelripley.elite_etl

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import net.michaelripley.elite_etl.dto.enums._

object JsonMapperFactory {
  def getInstance: ObjectMapper = {
    val objectMapper = new ObjectMapper()

    // configure the ObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
    objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false)
    objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)

    val module = new SimpleModule()
    module.addDeserializer(classOf[State], State.deserializer)
    module.addDeserializer(classOf[Allegiance], Allegiance.deserializer)
    module.addDeserializer(classOf[Economy], Economy.deserializer)
    module.addDeserializer(classOf[StationType], StationType.deserializer)
    module.addDeserializer(classOf[LandingPadSize],LandingPadSize.deserializer)
    objectMapper.registerModule(module)

    // return the ObjectMapper
    objectMapper
  }
}
