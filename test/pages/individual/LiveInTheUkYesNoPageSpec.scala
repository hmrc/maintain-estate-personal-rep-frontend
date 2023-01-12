/*
 * Copyright 2023 HM Revenue & Customs
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

package pages.individual

import models.{NonUkAddress, UkAddress}
import pages.behaviours.PageBehaviours

class LiveInTheUkYesNoPageSpec extends PageBehaviours {

  "LiveInTheUkYesNoPage" must {

    beRetrievable[Boolean](LiveInTheUkYesNoPage)

    beSettable[Boolean](LiveInTheUkYesNoPage)

    beRemovable[Boolean](LiveInTheUkYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(UkAddressPage, UkAddress("line1", "line2", None, None, "postcode")).success.value
        .set(LiveInTheUkYesNoPage, false)

      userAnswers.get.get(UkAddressPage) mustNot be(defined)
    }

    "implement cleanup logic when Yes selected" in {
      val userAnswers = emptyUserAnswers
        .set(NonUkAddressPage, NonUkAddress("line1", "line2", None,"country")).success.value
        .set(LiveInTheUkYesNoPage, true)

      userAnswers.get.get(NonUkAddressPage) mustNot be(defined)
    }

  }
}
