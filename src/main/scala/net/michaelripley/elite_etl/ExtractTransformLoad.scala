package net.michaelripley.elite_etl

import net.michaelripley.elite_etl.db.DatabaseConnector.{FactionPresence, StateMapping, StationEconomy}
import net.michaelripley.elite_etl.db.{DatabaseConnector, MockDatabaseConnectorImpl, PsqlDatabaseConnectorImpl}
import net.michaelripley.elite_etl.dto.enums.StationType
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

    val argsSet = args.toSet
    val force = argsSet.contains("force")
    val dryRun = argsSet.contains("dryRun")

    val exitCode = if (!force && allResources.forall(_.cached)) {
      println("all resources are up to date; nothing to do.")
      // return a different status code so that shell scripts can detect that nothing was done
      304
    } else {
      if (force) {
        println("ignoring etags and forcing update")
      }

      val db: DatabaseConnector = if (dryRun) {
        println("dry run: not writing to database")
        new MockDatabaseConnectorImpl()
      } else {
        new PsqlDatabaseConnectorImpl()
      }

      // I purposely did not import System to avoid hiding java.lang.System
      val systems = objectMapper.readValue(systemSource.inputStream, classOf[Array[dto.System]])
      db.writeSystems(systems)
      println(s"wrote ${systems.length} systems to database")

      val fleetCarrierStationType = StationType("Fleet Carrier")
      val stations = objectMapper.readValue(stationSource.inputStream, classOf[Array[Station]])
        .filterNot(_.stationType == fleetCarrierStationType) // remove fleet carriers as they can exist in unpopulated systems
        .filterNot(_.stationType == null) // remove unknown station types as they could be fleet carriers
      db.writeStations(stations)
      println(s"wrote ${stations.length} stations to database")

      val factions = objectMapper.readValue(factionSource.inputStream, classOf[Array[Faction]])
      db.writeFactions(factions)
      println(s"wrote ${factions.length} factions to database")

      // faction/system mappings
      val factionSystemMapping: Array[FactionPresence] = systems
        .view
        .flatMap(system => system.factionPresences.map(faction => (system.id, faction.id)))
        .toArray
        .distinct
      db.writeFactionPresences(factionSystemMapping)
      println(s"wrote ${factionSystemMapping.length} faction presences to database")

      // station/economy mappings
      val stationEconomyMapping: Array[StationEconomy] = stations
        .view
        .flatMap(station => station.economies.map(economy => (station.id, economy)))
        .toArray
        .distinct
      db.writeStationEconomies(stationEconomyMapping)
      println(s"wrote ${stationEconomyMapping.length} station economies to database")

      val stationStateMapping: Array[StateMapping] = stations
        .view
        .flatMap(station => station.states.map(state => (station.id, state.name)))
        .toArray
        .distinct
      db.writeStationStates(stationStateMapping)
      println(s"wrote ${stationStateMapping.length} station states to database")

      val systemStateMapping: Array[StateMapping] = systems
        .view
        .flatMap(system => system.states.map(state => (system.id, state.name)))
        .toArray
        .distinct
      db.writeSystemStates(systemStateMapping)
      println(s"wrote ${systemStateMapping.length} system states to database")

      0
    }

    val stopTime = System.nanoTime()
    val elapsedTime = (stopTime - startTime) / 1000000000d
    println(f"finished in $elapsedTime%.3fs")

    sys.exit(exitCode)
  }
}
