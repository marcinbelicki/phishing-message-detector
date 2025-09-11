package pl.belicki.detector.external

import com.google.inject.AbstractModule
import com.google.inject.name.{Named, Names}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration

class ExternalServiceModule extends AbstractModule {
  private lazy val config = ExternalServiceConfig(
    apiKey = ConfigFactory.load().getString("external.config.apiKey"),
    url = ConfigFactory.load().getString("external.config.url"),
    cacheTtl = {
      val javaDuration = ConfigFactory.load().getDuration("external.config.cacheTtl")
      Duration.fromNanos(javaDuration.toNanos)
    }
  )

  override def configure(): Unit = {
    bind(classOf[ExternalServiceConfig]).toInstance(config)
    bind(classOf[ExternalService]).annotatedWith(Names.named("underlying")).to(classOf[GoogleWebRiskAPI])
    bind(classOf[ExternalService]).annotatedWith(Names.named("usedExternalService")).to(classOf[MemoizingExternalService])
  }

}
