package net.michaelripley.elite_etl.db

import net.michaelripley.elite_etl.db.DatabaseConnector.{FactionPresence, StateMapping, StationEconomy}
import net.michaelripley.elite_etl.dto.enums.{Economy, StateName}
import net.michaelripley.elite_etl.dto.{Faction, Station, System}

object DatabaseConnector {
  type FactionPresence = (Int, Int)
  type StationEconomy = (Int, Economy)
  type StateMapping = (Int, StateName)
}

trait DatabaseConnector extends AutoCloseable {
  def writeSystems(systems: IterableOnce[System]): Unit
  def writeStations(stations: IterableOnce[Station]): Unit
  def writeFactions(factions: IterableOnce[Faction]): Unit
  def writeFactionPresences(factionPresences: IterableOnce[FactionPresence]): Unit
  def writeStationEconomies(stationEconomies: IterableOnce[StationEconomy]): Unit
  def writeStationStates(stationStates: IterableOnce[StateMapping]): Unit
  def writeSystemStates(systemStates: IterableOnce[StateMapping]): Unit
}
