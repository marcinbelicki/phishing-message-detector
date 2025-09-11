package pl.belicki.detector

class UrlExtractor {

  private val regex =
    """(?m)(?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]""".r

  def extractUrls(message: String): Iterator[String] =
    regex.findAllIn(message)

}
