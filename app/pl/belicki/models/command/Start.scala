package pl.belicki.models.command

import pl.belicki.controllers.CheckSMSController
import pl.belicki.models.Response

import scala.concurrent.{ExecutionContext, Future}

case class Start(phoneNumber: String) extends Command {
  override def execute(checkSMSController: CheckSMSController)(implicit ec: ExecutionContext): Future[Response] = ???
}

object Start {
  val COMMAND = "START"
}
