package pl.belicki.controllers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.google.inject._
import pl.belicki.config.ServiceConfig
import pl.belicki.database.DatabaseConfiguration
import pl.belicki.database.table.ClientNumber
import pl.belicki.detector.Detector
import pl.belicki.models.{Message, Response, ResponseStatus}
import play.api.{Logger, Logging}
import play.api.http.HttpErrorHandler
import play.api.libs.json.{JsNull, JsValue}
import play.api.mvc._
import play.json.mapper.JsonMapperExtensions

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class CheckSMSController @Inject() (
    val jsonMapper: JsonMapper with ClassTagExtensions,
    val controllerComponents: ControllerComponents,
    val serviceConfig: ServiceConfig,
    val clientNumber: ClientNumber,
    val databaseConfiguration: DatabaseConfiguration,
    val httpErrorHandler: HttpErrorHandler,
    val detector: Detector
) extends BaseController
    with JsonMapperExtensions
    with Logging {
  implicit lazy val ec: ExecutionContext = defaultExecutionContext

  import databaseConfiguration.profile.api._

  def serviceEnabled(
      number: String
  ): DBIOAction[Boolean, NoStream, Effect.Read] =
    clientNumber.query
      .filter(_.number === number)
      .exists
      .result

  def upsertNumber(
      number: String
  ): DBIOAction[
    Boolean,
    NoStream,
    Effect.Read with Effect.Write with Effect.Transactional
  ] = {
    for {
      exists <- clientNumber.query.filter(_.number === number).exists.result
      _ <-
        if (exists) DBIO.successful(null)
        else clientNumber.query += number
    } yield exists
  }.transactionally

  def removeNumber(
      number: String
  ): DBIOAction[Int, NoStream, Effect.Write] =
    clientNumber.query.filter(_.number === number).delete

  private def errorHandling(implicit
      requestHeader: RequestHeader
  ): PartialFunction[Throwable, Future[Result]] = { case NonFatal(e) =>
    Future.successful(
      InternalServerError(CheckSMSController.ErrorMessageOnly(e.getMessage))
    )
  }

  def check(): Action[Message] = Action.async(parse.jacksonJson[Message]) {
    implicit request: Request[Message] =>
      logger.info(s"Received request: $request")
      serviceConfig
        .toCommand(request.body)
        .execute(this)
        .map(Ok(_))
        .recoverWith(errorHandling)
  }
}

object CheckSMSController {
  case class ErrorMessageOnly(
      message: String
  )
}
