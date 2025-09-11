package pl.belicki.detector

import com.google.inject.Inject
import pl.belicki.detector.external.ExternalService

class Detector @Inject() (
    val externalService: ExternalService
) {

  def analyzeMessage(message: String)
}
