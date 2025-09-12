package pl.belicki.database

import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import pl.belicki.detector.external.{
  ExternalService,
  ExternalServiceConfig,
  MemoizingExternalService
}
import play.api.inject
import play.api.inject.{Binding, bind}

import scala.language.postfixOps

object ExternalServiceConfigTest {

  def bindings(underlyingExternalService: ExternalService): List[Binding[_]] =
    List(
      bind[ExternalService]
        .qualifiedWith("underlying")
        .toInstance(underlyingExternalService),
      bind[ExternalServiceConfig].toInstance(
        ExternalServiceConfig(
          apiKey = "",
          url = "",
          cacheTtl = 1 hour
        )
      ),
      bind[ExternalService]
        .qualifiedWith("usedExternalService")
        .to[MemoizingExternalService]
    )

}
