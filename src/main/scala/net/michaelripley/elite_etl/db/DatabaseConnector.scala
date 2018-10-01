package net.michaelripley.elite_etl.db

import java.io.FileInputStream
import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util.Properties

import net.michaelripley.elite_etl.dto.enums.Economy
import net.michaelripley.elite_etl.dto.{Faction, Station, System}

private object DatabaseConnector {
  type FactionPresence = (Int, Int)
  type StationEconomy = (Int, Economy)

  private val systemInsertQuery =
    """insert into system
      | (system_id, system_name, coordinates)
      | values (?, ?, ST_MakePoint(?, ?, ?));
      |""".stripMargin

  private val stationInsertQuery =
    """insert into station
      | (station_id, station_name, station_state, arrival_distance, system_id, max_landing_pad_size, has_docking, is_planetary)
      | values (?, ?, ?::state, ?, ?, ?::landing_pad_size, ?, ?);
      |""".stripMargin

  private val factionInsertQuery =
    """insert into faction
      | (faction_id, faction_name, faction_allegiance, faction_state)
      | values (?, ?, ?::major_power, ?::state);
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
}

class DatabaseConnector extends AutoCloseable {

  import DatabaseConnector._

  private val properties: Properties = new Properties()
  properties.load(new FileInputStream("config.properties"))

  private val connectionUrl: String = properties.getProperty("db.connection.url")

  // load driver
  Class.forName("org.postgresql.Driver")

  private val connection: Connection = DriverManager.getConnection(connectionUrl)


  def writeSystems(systems: TraversableOnce[System]): Unit = {
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

  def writeStations(stations: TraversableOnce[Station]): Unit = {
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
        statement.setString(3, station.state.fold(null.asInstanceOf[String])(_.name))
        statement.setInt(4, station.arrivalDistance)
        statement.setInt(5, station.systemId)
        statement.setString(6, maxLandingPadSize)
        statement.setBoolean(7, station.hasDocking)
        statement.setBoolean(8, station.isPlanetary)
      }
    )
    beginStatement.execute("begin;")
    commitStatement.execute("commit;")
  }

  def writeFactions(factions: TraversableOnce[Faction]): Unit = {
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
        statement.setString(4, faction.state.fold(null.asInstanceOf[String])(_.name))
      }
    )
    commitStatement.execute("commit;")
  }

  def writeFactionPresences(factionPresences: TraversableOnce[FactionPresence]): Unit = {
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

  def writeStationEconomies(stationEconomies: TraversableOnce[StationEconomy]): Unit = {
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

  private def executeBatch[T](
    statement: PreparedStatement,
    batch: TraversableOnce[T],
    setBatch: (PreparedStatement, T) => Unit
  ): Unit = {
    var count: Int = 0

    batch.foreach(
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

    if (count > 0) {
      statement.executeBatch()
    }
  }

  override def close(): Unit = {
    connection.close()
  }
}
