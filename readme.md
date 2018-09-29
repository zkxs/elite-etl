# Elite ETL
An extract/transform/load application that takes data from the [EDDB API] and loads it into a [PostgreSQL] database.

This allows you to run queries on the EDDN data that EDDB does not support.


## Setup
1. Install [PostgreSQL].
2. Install [PostGIS].
3. Set up the `elite` database using [elite.sql].
4. Set up your `config.properties` file, and place it the root directory
   of this project. <br />
   Example:
   ```properties
   db.connection.url=jdbc:postgresql://localhost:5432/elite\
     ?user=elite\
     &password=hunter1\
     &ssl=true\
     &sslMode=require\
     &sslfactory=org.postgresql.ssl.NonValidatingFactory\
     &sslhostnameverifier=net.michaelripley.elite_etl.db.AllowAllHostnameVerifier
   ```
5. Download `systems_populated.json`, `stations.json`, and `factions.json` from the [EDDB API] and place them in the root directory of this project.


## Building and Running
Build and run the project with [sbt].
```
$ sbt
sbt:elite-etl> compile
sbt:elite-etl> run
```

Typical output:
```
wrote 20534 systems
wrote 62313 stations
wrote 76469 factions
wrote 118674 faction presences
wrote 82430 station economies
finished in 21.757s
```

## Planned features
- Automatically download EDDB json files, and keep them until new ones are available. (They refresh once a day).


## Example queries
- [Empire/Federation rank grind locations][rank grind query]


[EDDB API]: https://eddb.io/api
[PostgreSQL]: https://www.postgresql.org/
[PostGIS]: https://postgis.net/
[sbt]: https://www.scala-sbt.org/
[elite.sql]: doc/elite.sql
[rank grind query]: doc/elite-rank-grind-query.sql
