package pl.belicki.detector.external

import pl.belicki.models.Response

import scala.concurrent.{ExecutionContext, Future}

trait ExternalService {

  def checkUrl(url: String)(implicit ec: ExecutionContext): Future[Response]

}
