package pl.belicki.models.command

import pl.belicki.controllers.CheckSMSController
import pl.belicki.models.{Response, ResponseStatus}

import scala.concurrent.{ExecutionContext, Future}

case class AnalyzeMessage(sender: String, content: String) extends Command {
  override def execute(
      checkSMSController: CheckSMSController
  )(implicit ec: ExecutionContext): Future[Response] = {
    import checkSMSController.databaseConfiguration.database
    for {
      serviceEnabled <- database.run(checkSMSController.serviceEnabled(sender))
    } yield {
      if (serviceEnabled) Response(ResponseStatus.NO_THREAT_DETECTED)
      else Response(ResponseStatus.SERVICE_DISABLED)
    }
  }
}
