package pl.belicki.detector.external

import scala.concurrent.duration.Duration

case class ExternalServiceConfig(
    apiKey: String,
    url: String,
    cacheTtl: Duration
)
