package pl.belicki.controllers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import pl.belicki.models.Message
import play.api.mvc._
import play.json.mapper.JsonMapperExtensions

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val jsonMapper: JsonMapper with ClassTagExtensions, val controllerComponents: ControllerComponents) extends BaseController with JsonMapperExtensions {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action(parse.jacksonJson[Message]) { implicit request: Request[Message] =>
    println(request.body)

    Ok(request.body.message(4).toString)
  }
}
