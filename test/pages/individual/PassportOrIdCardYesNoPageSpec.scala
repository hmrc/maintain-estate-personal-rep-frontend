/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import models.{IdCard, Passport}
import pages.behaviours.PageBehaviours
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage}
import pages.individual.amend.PassportOrIdCardDetailsYesNoPage


class PassportOrIdCardYesNoPageSpec extends PageBehaviours {

  "PassportOrIdCardYesNoPage" must {

    beRetrievable[Boolean](PassportOrIdCardDetailsYesNoPage)

    beSettable[Boolean](PassportOrIdCardDetailsYesNoPage)

    beRemovable[Boolean](PassportOrIdCardDetailsYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(PassportDetailsPage, Passport("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(PassportOrIdCardDetailsYesNoPage, false)

      userAnswers.get.get(PassportDetailsPage) mustNot be(defined)
    }

    "implement cleanup logic when Yes selected" in {
      val userAnswers = emptyUserAnswers
        .set(IdCardDetailsPage, IdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(PassportOrIdCardDetailsYesNoPage, true)

      userAnswers.get.get(IdCardDetailsPage) mustNot be(defined)
    }

  }
}
