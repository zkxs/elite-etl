package net.michaelripley.elite_etl

import net.michaelripley.elite_etl.db.DatabaseConnector
import net.michaelripley.elite_etl.dto.enums.Economy
import net.michaelripley.elite_etl.dto.{Faction, Station}
import net.michaelripley.elite_etl.eddb.EddbDownloader

object ExtractTransformLoad {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime()

    val objectMapper = JsonMapperFactory.getInstance

    val systemSource = EddbDownloader.systems.get()
    val stationSource = EddbDownloader.stations.get()
    val factionSource = EddbDownloader.factions.get()

    val allResources = Seq(
      systemSource,
      stationSource,
      factionSource
    )

    val exitCode = if (allResources.forall(_.cached)) {
      println("all resources are up to date; nothing to do.")
      // return a different status code so that shell scripts can detect that nothing was done
      304
    } else {
      val db = new DatabaseConnector()

      // I purposely did not import System to avoid hiding java.lang.System
      val systems = objectMapper.readValue(systemSource.inputStream, classOf[Array[dto.System]])
      db.writeSystems(systems)
      println(s"wrote ${systems.length} systems to database")

      val stations = objectMapper.readValue(stationSource.inputStream, classOf[Array[Station]])
      db.writeStations(stations)
      println(s"wrote ${stations.length} stations to database")

      val factions = objectMapper.readValue(factionSource.inputStream, classOf[Array[Faction]])
      db.writeFactions(factions)
      println(s"wrote ${factions.length} factions to database")

      // faction/system mappings
      val factionSystemMapping: Array[(Int, Int)] = systems
        .view
        .flatMap(system => system.factionPresences.map(faction => (system.id, faction.id)))
        .toArray
        .distinct
      db.writeFactionPresences(factionSystemMapping)
      println(s"wrote ${factionSystemMapping.length} faction presences to database")

      // station/economy mappings
      val stationEconomyMapping: Array[(Int, Economy)] = stations
        .view
        .flatMap(station => station.economies.map(economy => (station.id, economy)))
        .toArray
        .distinct
      db.writeStationEconomies(stationEconomyMapping)
      println(s"wrote ${stationEconomyMapping.length} station economies to database")

      0
    }

    val stopTime = System.nanoTime()
    val elapsedTime = (stopTime - startTime) / 1000000000d
    println(f"finished in $elapsedTime%.3fs")

    sys.exit(exitCode)
  }
}
