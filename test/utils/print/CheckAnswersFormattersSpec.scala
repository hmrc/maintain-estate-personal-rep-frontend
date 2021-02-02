/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
