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

package navigation

import base.SpecBase
import models.{CheckMode, NormalMode, PassportOrIdCard}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual._
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage}

class IndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new IndividualNavigator

  "Individual navigator" when {

    "add journey navigation" must {

      val mode = NormalMode

      "name page -> date of birth page" in {

        navigator.nextPage(NamePage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.DateOfBirthController.onPageLoad(mode))
      }

      "date of birth page -> Nino Yes No page" in {

        navigator.nextPage(DateOfBirthPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "Nino Yes No page -> Yes -> Nino page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individual.routes.NationalInsuranceNumberController.onPageLoad(mode))
      }

      "Nino Yes No page -> No -> Passport Or IDCard page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individual.routes.PassportOrIdCardController.onPageLoad(mode))
      }

      "Passport Or ID Card page -> Passport -> Passport page" in {
        val answers = emptyUserAnswers
          .set(PassportOrIdCardPage, PassportOrIdCard.Passport).success.value

        navigator.nextPage(PassportOrIdCardPage, mode, answers)
          .mustBe(controllers.individual.routes.PassportDetailsController.onPageLoad(mode))
      }

      "Passport Or ID Card page -> IdCard -> Passport page" in {
        val answers = emptyUserAnswers
          .set(PassportOrIdCardPage, PassportOrIdCard.IdCard).success.value

        navigator.nextPage(PassportOrIdCardPage, mode, answers)
          .mustBe(controllers.individual.routes.IdCardDetailsController.onPageLoad(mode))
      }

      "Passport page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(PassportDetailsPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "ID Card page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(IdCardDetailsPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Nino page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(NationalInsuranceNumberPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Live In UK page -> Yes -> UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, true).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individual.routes.UkAddressController.onPageLoad(mode))
      }

      "Live In UK page -> No -> Non UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individual.routes.NonUkAddressController.onPageLoad(mode))
      }

      "UK Address page -> Telephone number page" in {
        navigator.nextPage(UkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.TelephoneNumberController.onPageLoad(mode))
      }

      "None UK Address page -> Telephone number page" in {
        navigator.nextPage(NonUkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.routes.TelephoneNumberController.onPageLoad(mode))
      }

      "Telephone number page" when {

        "Normal mode" must {
          val mode = NormalMode

          "-> Start date page" in {
            navigator.nextPage(TelephoneNumberPage, mode, emptyUserAnswers)
              .mustBe(controllers.individual.add.routes.StartDateController.onPageLoad())
          }
        }

        "Check mode" must {
          val mode = CheckMode

          "-> Check amended answers page" in {
            navigator.nextPage(TelephoneNumberPage, CheckMode, emptyUserAnswers)
              .mustBe(controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers())
          }
        }
      }

      "Start date page -> Check details" in {
        navigator.nextPage(StartDatePage, mode, emptyUserAnswers)
          .mustBe(controllers.individual.add.routes.CheckDetailsController.onPageLoad())
      }

    }
  }
}
