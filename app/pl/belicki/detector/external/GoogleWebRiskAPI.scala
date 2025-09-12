package pl.belicki.detector.external

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.google.inject.Inject

import pl.belicki.models.{Response, ResponseStatus}
import play.api.Logging
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.ws.WSClient
import play.json.mapper.JsonMapperExtensions

import scala.concurrent.{ExecutionContext, Future}
import scala.math.Ordered.orderingToOrdered

class GoogleWebRiskAPI @Inject() (
    val wsClient: WSClient,
    val externalServiceConfig: ExternalServiceConfig,
    val jsonMapper: JsonMapper with ClassTagExtensions
) extends ExternalService
    with JsonMapperExtensions
    with Logging {

  private val MINIMAL_CONFIDENCE = ConfidenceLevel.VERY_HIGH

  private def createBody(url: String) =
    GoogleWebRiskAPI.Body(
      uri = url,
      threatTypes = List(
        "THREAT_TYPE_UNSPECIFIED",
        "SOCIAL_ENGINEERING",
        "MALWARE",
        "UNWANTED_SOFTWARE"
      ),
      allowScan = true
    )

  private def responseBodyToResponse(
      response: GoogleWebRiskAPI.Response
  ): Response = {
    if (response.scores.exists(_.confidenceLevel > MINIMAL_CONFIDENCE))
      Response(ResponseStatus.THREAT_DETECTED)
    else Response(ResponseStatus.NO_THREAT_DETECTED)
  }

  override def checkUrl(url: String)(implicit
      ec: ExecutionContext
  ): Future[Response] = {
    logger.info(s"Checking url: $url")

    wsClient
      .url(externalServiceConfig.url)
      .withHttpHeaders(
        "X-goog-api-key"         -> externalServiceConfig.apiKey,
        HeaderNames.CONTENT_TYPE -> s"${MimeTypes.JSON}; charset=utf-8"
      )
      .post(createBody(url))
      .flatMap {
        case body if body.status == 200 => Future.successful(body)
        case _ =>
          Future.failed(
            new RuntimeException(
              s"Couldn't get proper response via ${externalServiceConfig.url}"
            )
          )
      }
      .map(_.body(bodyReadable[GoogleWebRiskAPI.Response]))
      .recoverWith { case jsonParseException: JsonParseException =>
        Future.failed(
          new JsonParseException(
            s"Couldn't read the response from ${externalServiceConfig.url}: ${jsonParseException.getMessage}"
          )
        )
      }
      .map(responseBodyToResponse)
  }

}

object GoogleWebRiskAPI {
  case class Body(
      uri: String,
      threatTypes: List[String],
      allowScan: Boolean
  )

  case class Response(
      scores: List[Score]
  )

  case class Score(
      threatType: String,
      confidenceLevel: ConfidenceLevel
  )
}
