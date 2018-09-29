package net.michaelripley.elite_etl

import java.io.FileInputStream

import net.michaelripley.elite_etl.db.DatabaseConnector
import net.michaelripley.elite_etl.dto.enums.Economy
import net.michaelripley.elite_etl.dto.{Faction, Station}

object ExtractTransformLoad {
  def main(args: Array[String]): Unit = {
    val objectMapper = JsonMapperFactory.getInstance


    lazy val systemSource = new FileInputStream("""systems_populated.json""") // https://eddb.io/archive/v5/systems_populated.json
    lazy val stationSource = new FileInputStream("""stations.json""") // https://eddb.io/archive/v5/stations.json
    lazy val factionSource = new FileInputStream("""factions.json""") // https://eddb.io/archive/v5/factions.json

    val startTime = System.nanoTime()

    val db = new DatabaseConnector()

    val systems = objectMapper.readValue(systemSource, classOf[Array[dto.System]])
    db.writeSystems(systems)
    println(s"wrote ${systems.length} systems")

    // I purposely did not import System to avoid hiding java.lang.System
    val stations = objectMapper.readValue(stationSource, classOf[Array[Station]])
      .filter(_.hasDocking) // I do not consider things without docking to be stations
    db.writeStations(stations)
    println(s"wrote ${stations.length} stations")

    val factions = objectMapper.readValue(factionSource, classOf[Array[Faction]])
    db.writeFactions(factions)
    println(s"wrote ${factions.length} factions")

    // faction/system mappings
    val factionSystemMapping = systems
      .flatMap(system => system.factionPresences.map(faction => (system.id, faction.id)))
    db.writeFactionPresences(factionSystemMapping)
    println(s"wrote ${factionSystemMapping.length} faction presences")

    // station/economy mappings
    val stationEconomyMapping: Array[(Int, Economy)] = stations
      .flatMap(station => station.economies.map(economy => (station.id, economy)))
    db.writeStationEconomies(stationEconomyMapping)
    println(s"wrote ${stationEconomyMapping.length} station economies")

    val stopTime = System.nanoTime()

    val elapsedTime = (stopTime - startTime) / 1000000000d
    println(f"finished in $elapsedTime%.3fs")
  }
}
