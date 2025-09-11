package pl.belicki.models.command

import pl.belicki.controllers.CheckSMSController
import pl.belicki.models.{Response, ResponseStatus}

import scala.concurrent.{ExecutionContext, Future}

case class Start(phoneNumber: String) extends Command {
  override def execute(checkSMSController: CheckSMSController)(implicit ec: ExecutionContext): Future[Response] = {
    import checkSMSController.databaseConfiguration.database
    for {
      _ <- database.run(checkSMSController.upsertNumber(phoneNumber))
    } yield Response(ResponseStatus.SERVICE_TURNED_ON)
  }
}

object Start {
  val COMMAND = "START"
}
