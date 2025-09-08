package pl.belicki.controllers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import pl.belicki.models.Message
import play.api.mvc._
import play.json.mapper.JsonMapperExtensions

import javax.inject._
import scala.concurrent.Future

@Singleton
class HomeController @Inject() (
    val jsonMapper: JsonMapper with ClassTagExtensions,
    val controllerComponents: ControllerComponents
) extends BaseController
    with JsonMapperExtensions {
  def index(): Action[Message] = Action.async(parse.jacksonJson[Message]) {
    implicit request: Request[Message] =>
      println(request.body)

      Future.successful(Ok(request.body.message(4).toString))
  }
}
