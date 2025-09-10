package pl.belicki.database

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory

class DatabaseModule extends AbstractModule {
  private val databaseConfiguration = DatabaseConfiguration(
    databaseTypesafeConfig =
      ConfigFactory.load().getConfig("phishing_detector"),
    flywayTypesafeConfig = ConfigFactory.load().getConfig("flyway")
  )

  override def configure(): Unit =
    bind(classOf[DatabaseConfiguration]).toInstance(databaseConfiguration)
}
