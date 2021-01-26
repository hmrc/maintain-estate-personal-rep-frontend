package utils.print

import base.SpecBase
import play.twirl.api.Html

class CheckAnswersFormattersSpec extends SpecBase {

  "CheckAnswersFormatters" when {

    ".formatNino" must {

      "format a nino with prefix and suffix" in {
        val nino = "JP121212A"
        val result = CheckAnswersFormatters.formatNino(nino)
        result mustBe Html("JP 12 12 12 A")
      }

      "suppress IllegalArgumentException and not format nino" in {
        val nino = "JP121212"
        val result = CheckAnswersFormatters.formatNino(nino)
        result mustBe Html("JP121212")
      }

    }
  }

}
