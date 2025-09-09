package pl.belicki.config

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory

class ServiceConfigModule extends AbstractModule {

  private lazy val serviceConfig = ServiceConfig(
    servicePhoneNumber =
      ConfigFactory.load().getString("phishing.detector.service.number")
  )

  override def configure(): Unit =
    bind(classOf[ServiceConfig]).toInstance(serviceConfig)

}
