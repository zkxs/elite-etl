package net.michaelripley.elite_etl.db

import net.michaelripley.elite_etl.db.DatabaseConnector._
import net.michaelripley.elite_etl.dto.enums.{Economy, StateName}
import net.michaelripley.elite_etl.dto.{Faction, Station, System}

object DatabaseConnector {
  type FactionPresence = (Int, Int)
  type FactionPresenceState = (Int, Int, StateName)
  type StationEconomy = (Int, Economy)
  type StateMapping = (Int, StateName)
}

trait DatabaseConnector extends AutoCloseable {
  def writeSystems(systems: IterableOnce[System]): Unit
  def writeStations(stations: IterableOnce[Station]): Unit
  def writeFactions(factions: IterableOnce[Faction]): Unit
  def writeFactionPresences(factionPresences: IterableOnce[FactionPresence]): Unit
  def writeFactionPresenceStates(factionPresenceStates: IterableOnce[FactionPresenceState]): Unit
  def writeStationEconomies(stationEconomies: IterableOnce[StationEconomy]): Unit
  def writeStationStates(stationStates: IterableOnce[StateMapping]): Unit
  def writeSystemStates(systemStates: IterableOnce[StateMapping]): Unit
}
