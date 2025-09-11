package pl.belicki.detector.external

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.google.inject.{Inject, Provides}
import com.google.inject.name.Named
import pl.belicki.models.{Response, ResponseStatus}
import play.api.libs.ws.WSClient
import play.json.mapper.JsonMapperExtensions

import scala.concurrent.{ExecutionContext, Future}
import scala.math.Ordered.orderingToOrdered

@Provides
@Named("underlying")
class GoogleWebRiskAPI @Inject() (
    val wsClient: WSClient,
    val externalServiceConfig: ExternalServiceConfig,
    val jsonMapper: JsonMapper with ClassTagExtensions
) extends ExternalService
    with JsonMapperExtensions {

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

  def responseBodyToResponse(response: GoogleWebRiskAPI.Response) = {
    if (response.scores.exists(_.confidenceLevel > MINIMAL_CONFIDENCE)) Response(ResponseStatus.THREAT_DETECTED)
    else Response(ResponseStatus.NO_THREAT_DETECTED)
  }

  override def checkUrl(url: String)(implicit
      ec: ExecutionContext
  ): Future[Response] =
    wsClient
      .url(externalServiceConfig.url).post(createBody(url))
      .map(_.body[String])
      .

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
