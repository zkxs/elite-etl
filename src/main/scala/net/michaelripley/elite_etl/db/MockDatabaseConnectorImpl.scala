package net.michaelripley.elite_etl.db

import net.michaelripley.elite_etl.db.DatabaseConnector.{FactionPresence, StateMapping, StationEconomy}
import net.michaelripley.elite_etl.dto
import net.michaelripley.elite_etl.dto.enums.StateName
import net.michaelripley.elite_etl.dto.{Faction, Station}

class MockDatabaseConnectorImpl extends DatabaseConnector {
  override def writeSystems(systems: IterableOnce[dto.System]): Unit = ()

  override def writeStations(stations: IterableOnce[Station]): Unit = ()

  override def writeFactions(factions: IterableOnce[Faction]): Unit = ()

  override def writeFactionPresences(factionPresences: IterableOnce[FactionPresence]): Unit = ()

  override def writeFactionPresenceStates(factionPresenceStates: IterableOnce[(Int, Int, StateName)]): Unit = ()

  override def writeStationEconomies(stationEconomies: IterableOnce[StationEconomy]): Unit = ()

  override def writeStationStates(stationStates: IterableOnce[StateMapping]): Unit = ()

  override def writeSystemStates(systemStates: IterableOnce[StateMapping]): Unit = ()

  override def close(): Unit = ()
}
