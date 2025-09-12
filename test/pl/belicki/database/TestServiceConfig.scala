package pl.belicki.database

import pl.belicki.config.ServiceConfig
import play.api.inject.{Binding, bind}

object TestServiceConfig {
  val SERVICE_PHONE_NUMBER = "1234"
  lazy val bindings: List[Binding[ServiceConfig]] = List(
    bind[ServiceConfig].toInstance(ServiceConfig(SERVICE_PHONE_NUMBER))
  )

}
