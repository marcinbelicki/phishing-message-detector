package pl.belicki.detector

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpecLike

class UrlExtractorTest extends AnyWordSpecLike {

  "UrlExtractor" must {
    "properly extract urls" in {
      val urlExtractor = new UrlExtractor

      val extracted = urlExtractor
        .extractUrls(
          "This url is a threat: https://www.playframework.com/threat, and this is not:\n ;;;https://www.playframework.com/ok "
        )
        .toList

      extracted shouldBe List(
        "https://www.playframework.com/threat",
        "https://www.playframework.com/ok"
      )
    }
  }

}
