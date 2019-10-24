package net.michaelripley.elite_etl.db

import java.io.FileInputStream
import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util.Properties

import net.michaelripley.elite_etl.db.DatabaseConnector.{FactionPresence, StateMapping, StationEconomy}
import net.michaelripley.elite_etl.dto.{Faction, Station, System}

private object PsqlDatabaseConnectorImpl {

  private val systemInsertQuery =
    """insert into system
      | (system_id, system_name, coordinates)
      | values (?, ?, ST_MakePoint(?, ?, ?));
      |""".stripMargin

  private val stationInsertQuery =
    """insert into station
      | (station_id, station_name, arrival_distance, system_id, max_landing_pad_size, has_docking, is_planetary)
      | values (?, ?, ?, ?, ?::landing_pad_size, ?, ?);
      |""".stripMargin

  private val factionInsertQuery =
    """insert into faction
      | (faction_id, faction_name, faction_allegiance)
      | values (?, ?, ?::major_power);
      |""".stripMargin

  private val factionPresenceInsertQuery =
    """insert into faction_presence
      | (system_id, faction_id)
      | values (?, ?);
      |""".stripMargin

  private val stationEconomyInsertQuery =
    """insert into station_economy
      | (station_id, station_economy)
      | values (?, ?::economy);
      |""".stripMargin

  private val stationStateInsertQuery =
    """insert into station_state
      | (station_id, station_state)
      | values (?, ?::state);
      |""".stripMargin

  private val systemStateInsertQuery =
    """insert into system_state
      | (system_id, system_state)
      | values (?, ?::state);
      |""".stripMargin
}

class PsqlDatabaseConnectorImpl extends DatabaseConnector {

  import PsqlDatabaseConnectorImpl._

  private val properties: Properties = new Properties()
  properties.load(new FileInputStream("config.properties"))

  private val connectionUrl: String = properties.getProperty("db.connection.url")

  // load driver
  Class.forName("org.postgresql.Driver")

  private val connection: Connection = DriverManager.getConnection(connectionUrl)


  override def writeSystems(systems: IterableOnce[System]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val truncateStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(systemInsertQuery)

    beginStatement.execute("begin;")
    truncateStatement.execute("truncate system cascade;") // cascades to station and faction_presence
    executeBatch(
      insertStatement, systems, (statement, system: System) => {
        statement.setInt(1, system.id)
        statement.setString(2, system.name)
        statement.setDouble(3, system.x)
        statement.setDouble(4, system.y)
        statement.setDouble(5, system.z)
      }
    )
    commitStatement.execute("commit;")
  }

  override def writeStations(stations: IterableOnce[Station]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(stationInsertQuery)
    executeBatch(
      insertStatement, stations, (statement, station: Station) => {

        // maxLandingPadSize can sometimes be null
        val maxLandingPadSize = if (station.maxLandingPadSize == null) {
          null
        } else {
          station.maxLandingPadSize.name
        }

        statement.setInt(1, station.id)
        statement.setString(2, station.name)
        statement.setInt(3, station.arrivalDistance)
        statement.setInt(4, station.systemId)
        statement.setString(5, maxLandingPadSize)
        statement.setBoolean(6, station.hasDocking)
        statement.setBoolean(7, station.isPlanetary)
      }
    )
    beginStatement.execute("begin;")
    commitStatement.execute("commit;")
  }

  override def writeFactions(factions: IterableOnce[Faction]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val truncateStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(factionInsertQuery)

    beginStatement.execute("begin;")
    truncateStatement.execute("truncate faction cascade;") // cascades to faction_presence
    executeBatch(
      insertStatement, factions, (statement, faction: Faction) => {

        // allegiance can sometimes be null
        val allegiance = if (faction.allegiance == null) {
          null
        } else {
          faction.allegiance.name
        }

        statement.setInt(1, faction.id)
        statement.setString(2, faction.name)
        statement.setString(3, allegiance)
      }
    )
    commitStatement.execute("commit;")
  }

  override def writeFactionPresences(factionPresences: IterableOnce[FactionPresence]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(factionPresenceInsertQuery)

    beginStatement.execute("begin;")
    executeBatch(
      insertStatement, factionPresences, (statement, factionPresence: FactionPresence) => {
        statement.setInt(1, factionPresence._1) // first item is system id
        statement.setInt(2, factionPresence._2) // second item is minor faction id
      }
    )
    commitStatement.execute("commit;")
  }

  override def writeStationEconomies(stationEconomies: IterableOnce[StationEconomy]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(stationEconomyInsertQuery)

    beginStatement.execute("begin;")
    executeBatch(
      insertStatement, stationEconomies, (statement, stationEconomy: StationEconomy) => {
        statement.setInt(1, stationEconomy._1) // first item is station id
        statement.setString(2, stationEconomy._2.name) // second item is economy name
      }
    )
    commitStatement.execute("commit;")
  }

  override def writeStationStates(stationStates: IterableOnce[StateMapping]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(stationStateInsertQuery)

    beginStatement.execute("begin;")
    executeBatch(
      insertStatement, stationStates, (statement, stationState: StateMapping) => {
        statement.setInt(1, stationState._1) // first item is station id
        statement.setString(2, stationState._2.name) // second item is state name
      }
    )
    commitStatement.execute("commit;")
  }

  override def writeSystemStates(systemStates: IterableOnce[StateMapping]): Unit = {
    val beginStatement = connection.createStatement()
    val commitStatement = connection.createStatement()
    val insertStatement = connection.prepareStatement(systemStateInsertQuery)

    beginStatement.execute("begin;")
    executeBatch(
      insertStatement, systemStates, (statement, systemState: StateMapping) => {
        statement.setInt(1, systemState._1) // first item is system id
        statement.setString(2, systemState._2.name) // second item is state name

      }
    )
    commitStatement.execute("commit;")
  }

  private def executeBatch[T](
    statement: PreparedStatement,
    batch: IterableOnce[T],
    setBatch: (PreparedStatement, T) => Unit
  ): Unit = {
    var count: Int = 0

    batch.iterator.foreach(
      item => {
        setBatch(statement, item)
        statement.addBatch()
        count += 1
        if (count >= 500) {
          statement.executeBatch()
          statement.clearBatch()
          count = 0
        }
      }
    )

    // last batch may range from empty up to full size
    if (count > 0) {
      statement.executeBatch()
    }
  }

  override def close(): Unit = {
    connection.close()
  }
}
