package pl.belicki.database

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import pl.belicki.database.DatabaseConfiguration
import play.api.inject.{Binding, bind}

import scala.jdk.CollectionConverters.IterableHasAsJava

object TestDatabaseConfig {

  private lazy val container = {
    val innerContainer = new PostgreSQLContainer(
      DockerImageName.parse("postgres:16-alpine")
    )
    innerContainer.start()
    innerContainer
  }

  private lazy val databaseTypesafeConfig: Config = {
    ConfigFactory
      .empty("phishing_detector")
      .withValue(
        "db.properties.serverName",
        ConfigValueFactory.fromAnyRef(container.getHost)
      )
      .withValue(
        "db.properties.portNumber",
        ConfigValueFactory.fromAnyRef(
          container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
        )
      )
      .withValue(
        "db.properties.databaseName",
        ConfigValueFactory.fromAnyRef(
          container.getDatabaseName
        )
      )
      .withValue(
        "db.properties.user",
        ConfigValueFactory.fromAnyRef(
          container.getUsername
        )
      )
      .withValue(
        "db.properties.password",
        ConfigValueFactory.fromAnyRef(
          container.getPassword
        )
      )
      .withValue(
        "profile",
        ConfigValueFactory.fromAnyRef("slick.jdbc.PostgresProfile$")
      )
      .withValue("db.connectionPool", ConfigValueFactory.fromAnyRef("HikariCP"))
      .withValue(
        "db.dataSourceClass",
        ConfigValueFactory.fromAnyRef("org.postgresql.ds.PGSimpleDataSource")
      )
  }

  private lazy val flywayTypesafeConfig: Config =
    ConfigFactory
      .empty()
      .withValue("url", ConfigValueFactory.fromAnyRef(container.getJdbcUrl))
      .withValue(
        "driver",
        ConfigValueFactory.fromAnyRef("org.postgresql.Driver")
      )
      .withValue(
        "locations",
        ConfigValueFactory.fromIterable(
          Iterable("classpath:database/migrations").asJava
        )
      )

  lazy val bindings: Seq[Binding[DatabaseConfiguration]] = List(
    bind[DatabaseConfiguration].toInstance(
      DatabaseConfiguration(
        databaseTypesafeConfig = databaseTypesafeConfig,
        flywayTypesafeConfig = flywayTypesafeConfig
      )
    )
  )

}
