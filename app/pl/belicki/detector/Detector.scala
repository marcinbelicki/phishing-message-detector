package pl.belicki.detector

import com.google.inject.Inject
import com.google.inject.name.Named
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import pl.belicki.detector.external.ExternalService
import pl.belicki.models.{Response, ResponseStatus}
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}

class Detector @Inject() (
    @Named("usedExternalService") val externalService: ExternalService,
    val urlExtractor: UrlExtractor
)(implicit val as: ActorSystem)
    extends Logging {

  def analyzeMessage(
      message: String
  )(implicit ec: ExecutionContext): Future[Response] = {

    logger.info(s"Analyzing message: $message")

    Source
      .fromIterator(() => urlExtractor.extractUrls(message))
      .mapAsync(1)(externalService.checkUrl)
      .dropWhile(_.status != ResponseStatus.THREAT_DETECTED)
      .runWith(Sink.headOption)
      .map(_.getOrElse(Response(ResponseStatus.NO_THREAT_DETECTED)))
  }

}
