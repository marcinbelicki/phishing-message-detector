package pl.belicki.detector

import scala.util.matching.Regex

class UrlExtractor {

  private val regex =
    """(?m)(?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]""".r

  def extractUrls(message: String): Regex.MatchIterator =
    regex.findAllIn(message)

}
