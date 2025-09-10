package pl.belicki.models.command

import pl.belicki.controllers.CheckSMSController
import pl.belicki.models.Response

import scala.concurrent.{ExecutionContext, Future}

trait Command {
  def execute(checkSMSController: CheckSMSController)(implicit ec: ExecutionContext): Future[Response]
}
