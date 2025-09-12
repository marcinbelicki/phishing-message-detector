package pl.belicki.controllers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, JavaTypeable}
import org.scalatest.Assertion
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import pl.belicki.controllers.CheckSMSController
import pl.belicki.database.{ExternalServiceConfigTest, TestDatabaseConfig, TestServiceConfig}
import pl.belicki.detector.external.{ExternalService, ExternalServiceConfig}
import pl.belicki.models.{Message, Response, ResponseStatus}
import play.api.Application
import play.api.Play.materializer
import play.api.http.{HeaderNames, MimeTypes}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test._
import play.api.test.Helpers._

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.{ExecutionContext, Future}

// run with -Dconfig.file=conf/test/application.conf
class CheckSMSControllerTest
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  private object UnderlyingExternalService extends ExternalService {

    private val countedUrl = "https://www.playframework.com/threat"
    val counter            = new AtomicInteger()

    private val map = Map(
      "https://www.playframework.com/threat" -> Response(
        ResponseStatus.THREAT_DETECTED
      ),
      "https://www.playframework.com/ok" -> Response(
        ResponseStatus.NO_THREAT_DETECTED
      )
    )

    override def checkUrl(url: String)(implicit
        ec: ExecutionContext
    ): Future[Response] = {
      if (url == countedUrl) counter.incrementAndGet()
      Future(map(url))
    }
  }

  private lazy val jsonMapper =
    app.injector.instanceOf[JsonMapper with ClassTagExtensions]

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .overrides(TestDatabaseConfig.bindings)
    .overrides(ExternalServiceConfigTest.bindings(UnderlyingExternalService))
    .overrides(TestServiceConfig.bindings)
    .build()

  private def readJson[T: JavaTypeable](result: Future[Result]): T =
    jsonMapper.readValue(contentAsString(result))

  private lazy val controller = app.injector.instanceOf[CheckSMSController]

  "CheckSMSController POST" should {

    "response with proper statuses" in {


      // when
      val serviceTurnedOn = sendMessage(
        Message(
          sender = "123456",
          recipient = TestServiceConfig.SERVICE_PHONE_NUMBER,
          message = "START"
        )
      )

      // then
      checkResponse(serviceTurnedOn, ResponseStatus.SERVICE_TURNED_ON)

      // when
      val threatDetected = sendMessage(
        Message(
          sender = "121412342345",
          recipient = "123456",
          message =
            "This url is a threat: https://www.playframework.com/threat, and this is not:\n https://www.playframework.com/ok "
        )
      )

      // then
      checkResponse(threatDetected, ResponseStatus.THREAT_DETECTED)

      // when
      val noThreatDetected = sendMessage(
        Message(
          sender = "121412342345",
          recipient = "123456",
          message = "and this is not:\n https://www.playframework.com/ok "
        )
      )

      // then
      checkResponse(noThreatDetected, ResponseStatus.NO_THREAT_DETECTED)

      // when
      val threatDetected2 = sendMessage(
        Message(
          sender = "121412342345",
          recipient = "123456",
          message = "and this is not:\n https://www.playframework.com/threat "
        )
      )

      // then
      checkResponse(threatDetected2, ResponseStatus.THREAT_DETECTED)

      // when
      val serviceTurnedOff = sendMessage(
        Message(
          sender = "123456",
          recipient = TestServiceConfig.SERVICE_PHONE_NUMBER,
          message = "STOP"
        )
      )

      // then
      checkResponse(serviceTurnedOff, ResponseStatus.SERVICE_TURNED_OFF)

      // when
      val serviceDisabled = sendMessage(
        Message(
          sender = "121412342345",
          recipient = "123456",
          message = "and this is not:\n https://www.playframework.com/threat "
        )
      )

      // then
      checkResponse(serviceDisabled, ResponseStatus.SERVICE_DISABLED)

      // when / then
      UnderlyingExternalService.counter.get() mustBe 1
    }

  }

  private def sendMessage(message: Message) = {
    controller
      .check()
      .apply(
        FakeRequest(POST, "/")
          .withHeaders(
            HeaderNames.ACCEPT       -> MimeTypes.JSON,
            HeaderNames.CONTENT_TYPE -> MimeTypes.JSON
          )
          .withBody(
            message
          )
      )
  }

  private def checkResponse(
      serviceTurnedOn: Future[Result],
      responseStatus: ResponseStatus
  ): Assertion = {
    status(serviceTurnedOn) mustBe OK
    contentType(serviceTurnedOn) must contain(MimeTypes.JSON)
    readJson[Response](serviceTurnedOn) mustBe Response(responseStatus)
  }
}
