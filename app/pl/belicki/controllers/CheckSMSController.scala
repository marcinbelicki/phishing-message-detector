package pl.belicki.controllers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import pl.belicki.config.ServiceConfig
import pl.belicki.models.{Message, Response, ResponseStatus}
import play.api.libs.json.{JsNull, JsValue}
import play.api.mvc._
import play.json.mapper.JsonMapperExtensions

import javax.inject._
import scala.concurrent.Future

@Singleton
class CheckSMSController @Inject() (
    val jsonMapper: JsonMapper with ClassTagExtensions,
    val controllerComponents: ControllerComponents,
    val serviceConfig: ServiceConfig
) extends BaseController
    with JsonMapperExtensions {
  def check(): Action[Message] = Action.async(parse.jacksonJson[Message]) {
    implicit request: Request[Message] =>
      println(request.body)
      Future.successful(Ok(Response(ResponseStatus.SERVICE_TURNED_ON)))
  }
}
