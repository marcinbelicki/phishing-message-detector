package pl.belicki.models.command

import pl.belicki.controllers.CheckSMSController
import pl.belicki.models.{Response, ResponseStatus}

import scala.concurrent.{ExecutionContext, Future}

case class AnalyzeMessage(recipient: String, content: String) extends Command {
  override def execute(
      checkSMSController: CheckSMSController
  )(implicit ec: ExecutionContext): Future[Response] = {
    import checkSMSController.databaseConfiguration.database
    for {
      serviceEnabled <- database.run(
        checkSMSController.serviceEnabled(recipient)
      )
      response <-
        if (serviceEnabled)
          checkSMSController.detector.analyzeMessage(content)
        else Future.successful(Response(ResponseStatus.SERVICE_DISABLED))
    } yield response
  }
}
