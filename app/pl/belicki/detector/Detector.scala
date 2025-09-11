package pl.belicki.detector

import com.google.inject.Inject
import com.google.inject.name.Named
import pl.belicki.detector.external.ExternalService
import pl.belicki.models.{Response, ResponseStatus}

import scala.concurrent.{ExecutionContext, Future}

class Detector @Inject() (
    @Named("usedExternalService") val externalService: ExternalService,
    val urlExtractor: UrlExtractor
) {

  def analyzeMessage(
      message: String
  )(implicit ec: ExecutionContext): Future[Response] = {

    Future
      .find(
        urlExtractor.extractUrls(message).map(externalService.checkUrl)
      )(_.status == ResponseStatus.THREAT_DETECTED)
      .map(_.getOrElse(Response(ResponseStatus.NO_THREAT_DETECTED)))

  }

}
