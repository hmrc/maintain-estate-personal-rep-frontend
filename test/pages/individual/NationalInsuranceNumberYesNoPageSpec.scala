/*
 * Copyright 2024 HM Revenue & Customs
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

import models.{CombinedPassportOrIdCard, IdCard, Passport, PassportOrIdCard}
import pages.behaviours.PageBehaviours
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage, PassportOrIdCardPage}
import pages.individual.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}


class NationalInsuranceNumberYesNoPageSpec extends PageBehaviours {

  "NationalInsuranceNumberYesNoPage" must {

    beRetrievable[Boolean](NationalInsuranceNumberYesNoPage)

    beSettable[Boolean](NationalInsuranceNumberYesNoPage)

    beRemovable[Boolean](NationalInsuranceNumberYesNoPage)

    "implement cleanup logic when No selected" in {
      val userAnswers = emptyUserAnswers
        .set(NationalInsuranceNumberPage, "AA000000A").success.value
        .set(NationalInsuranceNumberYesNoPage, false)

      userAnswers.get.get(NationalInsuranceNumberPage) mustNot be(defined)
    }

    "implement cleanup logic when Yes selected" in {
      val userAnswers = emptyUserAnswers
        .set(PassportDetailsPage, Passport("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(IdCardDetailsPage, IdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(PassportOrIdCardDetailsYesNoPage, false).success.value
        .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(PassportOrIdCardPage, PassportOrIdCard.Passport).success.value
        .set(NationalInsuranceNumberYesNoPage, true).success.value

      userAnswers.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
      userAnswers.get(PassportOrIdCardDetailsPage) mustNot be(defined)
      userAnswers.get(PassportDetailsPage) mustNot be(defined)
      userAnswers.get(IdCardDetailsPage) mustNot be(defined)
      userAnswers.get(PassportOrIdCardPage) mustNot be(defined)
    }

  }
}
