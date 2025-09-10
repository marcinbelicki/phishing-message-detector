package pl.belicki.database


import com.typesafe.config.Config
import org.flywaydb.core.Flyway
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.jdk.CollectionConverters.CollectionHasAsScala

class DatabaseConfiguration(
    databaseConfig: DatabaseConfig[JdbcProfile],
    flyway: Flyway
) {

  implicit val database: JdbcBackend#Database = {
    flyway.migrate()
    databaseConfig.db
  }

  lazy val profile: JdbcProfile = databaseConfig.profile
}

object DatabaseConfiguration {
  def apply(
      databaseTypesafeConfig: Config,
      flywayTypesafeConfig: Config
  ): DatabaseConfiguration = {
    val flyway = Flyway
      .configure()
      .driver(flywayTypesafeConfig.getString("driver"))
      .dataSource(
        flywayTypesafeConfig.getString("url"),
        databaseTypesafeConfig.getString("db.properties.user"),
        databaseTypesafeConfig.getString("db.properties.password")
      )
      .locations(
        flywayTypesafeConfig.getStringList("locations").asScala.toList: _*
      )
      .load

    new DatabaseConfiguration(
      DatabaseConfig.forConfig[JdbcProfile]("", databaseTypesafeConfig),
      flyway
    )
  }
}
