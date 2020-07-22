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
import controllers.individual.{routes => rts}
import controllers.individual.add.{routes => addRts}
import controllers.individual.amend.{routes => amendRts}
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
          .mustBe(rts.DateOfBirthController.onPageLoad(mode))
      }

      "date of birth page -> Nino Yes No page" in {

        navigator.nextPage(DateOfBirthPage, mode, emptyUserAnswers)
          .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "Nino Yes No page -> Yes -> Nino page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(rts.NationalInsuranceNumberController.onPageLoad(mode))
      }

      "Nino Yes No page -> No -> Passport Or IDCard page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(addRts.PassportOrIdCardController.onPageLoad())
      }

      "Passport Or ID Card page -> Passport -> Passport page" in {
        val answers = emptyUserAnswers
          .set(PassportOrIdCardPage, PassportOrIdCard.Passport).success.value

        navigator.nextPage(PassportOrIdCardPage, mode, answers)
          .mustBe(addRts.PassportDetailsController.onPageLoad())
      }

      "Passport Or ID Card page -> IdCard -> Passport page" in {
        val answers = emptyUserAnswers
          .set(PassportOrIdCardPage, PassportOrIdCard.IdCard).success.value

        navigator.nextPage(PassportOrIdCardPage, mode, answers)
          .mustBe(addRts.IdCardDetailsController.onPageLoad())
      }

      "Passport page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(PassportDetailsPage, mode, emptyUserAnswers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "ID Card page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(IdCardDetailsPage, mode, emptyUserAnswers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Nino page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(NationalInsuranceNumberPage, mode, emptyUserAnswers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Live In UK page -> Yes -> UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, true).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(rts.UkAddressController.onPageLoad(mode))
      }

      "Live In UK page -> No -> Non UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(rts.NonUkAddressController.onPageLoad(mode))
      }

      "UK Address page -> Telephone number page" in {
        navigator.nextPage(UkAddressPage, mode, emptyUserAnswers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(mode))
      }

      "None UK Address page -> Telephone number page" in {
        navigator.nextPage(NonUkAddressPage, mode, emptyUserAnswers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(mode))
      }

      "Telephone number page -> Start date page" in {

        navigator.nextPage(TelephoneNumberPage, mode, emptyUserAnswers)
          .mustBe(addRts.StartDateController.onPageLoad())
      }

      "Start date page -> Check details" in {
        navigator.nextPage(StartDatePage, mode, emptyUserAnswers)
          .mustBe(addRts.CheckDetailsController.onPageLoad())
      }

    }

    "amend journey navigation" must {

      val mode = CheckMode

      "name page -> date of birth page" in {

        navigator.nextPage(NamePage, mode, emptyUserAnswers)
          .mustBe(rts.DateOfBirthController.onPageLoad(mode))
      }

      "date of birth page -> Nino Yes No page" in {

        navigator.nextPage(DateOfBirthPage, mode, emptyUserAnswers)
          .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "Nino Yes No page -> Yes -> Nino page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(rts.NationalInsuranceNumberController.onPageLoad(mode))
      }

      "Nino Yes No page -> No -> Passport Or ID Card details page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(amendRts.PassportOrIdCardDetailsController.onPageLoad())
      }

      "Passport or ID card details page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(PassportOrIdCardDetailsPage, mode, emptyUserAnswers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Nino page -> LiveInTheUK Yes No page" in {

        navigator.nextPage(NationalInsuranceNumberPage, mode, emptyUserAnswers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Live In UK page -> Yes -> UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, true).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(rts.UkAddressController.onPageLoad(mode))
      }

      "Live In UK page -> No -> Non UK Address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(rts.NonUkAddressController.onPageLoad(mode))
      }

      "UK Address page -> Telephone number page" in {
        navigator.nextPage(UkAddressPage, mode, emptyUserAnswers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(mode))
      }

      "None UK Address page -> Telephone number page" in {
        navigator.nextPage(NonUkAddressPage, mode, emptyUserAnswers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(mode))
      }

      "Telephone number page -> Check details" in {
        navigator.nextPage(TelephoneNumberPage, mode, emptyUserAnswers)
          .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers())
      }
    }
  }
}
