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

package utils.print

import java.time.LocalDate

import controllers.individual.add.{routes => addRts}
import controllers.individual.amend.{routes => amendRts}
import controllers.individual.{routes => rts}
import base.SpecBase
import models.IndividualOrBusiness.Individual
import models.{CheckMode, CombinedPassportOrIdCard, IdCard, Name, NonUkAddress, NormalMode, Passport, PassportOrIdCard, UkAddress}
import pages.IndividualOrBusinessPage
import pages.individual._
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class IndividualPrintHelperSpec extends SpecBase {

  private val name: Name = Name("John", None, "Doe")
  private val displayName: String = name.displayFullName
  private val nino = "AA000000A"
  private val dateOfBirth = LocalDate.parse("1996-03-09")
  private val startDate = LocalDate.parse("2020-01-01")
  private val ukAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val telephoneNumber: String = "999"
  private val nonUkAddress = NonUkAddress("value 1", "value 2", None, "FR")
  private val passport: Passport = Passport("FR", "123", LocalDate.parse("2022-02-03"))
  private val idCard: IdCard = IdCard("FR", "123", LocalDate.parse("2022-02-03"))
  private val passportOrIdCard: CombinedPassportOrIdCard = CombinedPassportOrIdCard("FR", "123", LocalDate.parse("2022-02-03"))

  "Individual print helper" must {

    val helper = injector.instanceOf[IndividualPrintHelper]

    "generate add individual personal rep section" when {

      val mode = NormalMode
      val isProvisional = true

      val baseAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Individual).success.value
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(TelephoneNumberPage, telephoneNumber).success.value
        .set(StartDatePage, startDate).success.value

      "NINO and UK address" in {

        val userAnswers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value
          .set(NationalInsuranceNumberPage, nino).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value

        val result = helper(userAnswers, isProvisional, displayName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.dateOfBirth.checkYourAnswersLabel", displayName)), answer = Html("9 March 1996"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", displayName)), answer = Html("Yes"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumber.checkYourAnswersLabel", displayName)), answer = Html("AA 00 00 00 A"), changeUrl = rts.NationalInsuranceNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.liveInTheUkYesNo.checkYourAnswersLabel", displayName)), answer = Html("Yes"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.ukAddress.checkYourAnswersLabel", displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = rts.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.telephoneNumber.checkYourAnswersLabel", displayName)), answer = Html("999"), changeUrl = rts.TelephoneNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.startDate.checkYourAnswersLabel", displayName)), answer = Html("1 January 2020"), changeUrl = addRts.StartDateController.onPageLoad().url)
          )
        )
      }

      "Passport and UK address" in {

        val userAnswers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(PassportOrIdCardPage, PassportOrIdCard.Passport).success.value
          .set(PassportDetailsPage, passport).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value

        val result = helper(userAnswers, isProvisional, name.displayFullName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.dateOfBirth.checkYourAnswersLabel", displayName)), answer = Html("9 March 1996"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", displayName)), answer = Html("No"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("passportOrIdCard.checkYourAnswersLabel", displayName)), answer = Html("Passport"), changeUrl = addRts.PassportOrIdCardController.onPageLoad().url),
            AnswerRow(label = Html(messages("individual.passportDetails.checkYourAnswersLabel", displayName)), answer = Html("France<br />123<br />3 February 2022"), changeUrl = addRts.PassportDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individual.liveInTheUkYesNo.checkYourAnswersLabel", displayName)), answer = Html("Yes"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.ukAddress.checkYourAnswersLabel", displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = rts.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.telephoneNumber.checkYourAnswersLabel", displayName)), answer = Html("999"), changeUrl = rts.TelephoneNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.startDate.checkYourAnswersLabel", displayName)), answer = Html("1 January 2020"), changeUrl = addRts.StartDateController.onPageLoad().url)
          )
        )
      }

      "ID card and Non-UK address" in {

        val userAnswers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(PassportOrIdCardPage, PassportOrIdCard.IdCard).success.value
          .set(IdCardDetailsPage, idCard).success.value
          .set(LiveInTheUkYesNoPage, false).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value

        val result = helper(userAnswers, isProvisional, name.displayFullName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.dateOfBirth.checkYourAnswersLabel", displayName)), answer = Html("9 March 1996"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", displayName)), answer = Html("No"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("passportOrIdCard.checkYourAnswersLabel", displayName)), answer = Html("ID Card"), changeUrl = addRts.PassportOrIdCardController.onPageLoad().url),
            AnswerRow(label = Html(messages("individual.idCardDetails.checkYourAnswersLabel", displayName)), answer = Html("France<br />123<br />3 February 2022"), changeUrl = addRts.IdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individual.liveInTheUkYesNo.checkYourAnswersLabel", displayName)), answer = Html("No"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nonUkAddress.checkYourAnswersLabel", displayName)), answer = Html("value 1<br />value 2<br />France"), changeUrl = rts.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.telephoneNumber.checkYourAnswersLabel", displayName)), answer = Html("999"), changeUrl = rts.TelephoneNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.startDate.checkYourAnswersLabel", displayName)), answer = Html("1 January 2020"), changeUrl = addRts.StartDateController.onPageLoad().url)
          )
        )
      }
    }

    "generate amend individual personal rep section" when {

      val mode = CheckMode
      val isProvisional = false

      val baseAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Individual).success.value
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(TelephoneNumberPage, telephoneNumber).success.value
        .set(StartDatePage, startDate).success.value

      "NINO and UK address" in {

        val userAnswers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value
          .set(NationalInsuranceNumberPage, nino).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value

        val result = helper(userAnswers, isProvisional, displayName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.dateOfBirth.checkYourAnswersLabel", displayName)), answer = Html("9 March 1996"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", displayName)), answer = Html("Yes"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumber.checkYourAnswersLabel", displayName)), answer = Html("AA 00 00 00 A"), changeUrl = rts.NationalInsuranceNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.liveInTheUkYesNo.checkYourAnswersLabel", displayName)), answer = Html("Yes"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.ukAddress.checkYourAnswersLabel", displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = rts.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.telephoneNumber.checkYourAnswersLabel", displayName)), answer = Html("999"), changeUrl = rts.TelephoneNumberController.onPageLoad(mode).url)
          )
        )
      }

      "Passport/ID card and non-UK address" in {

        val userAnswers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(PassportOrIdCardDetailsPage, passportOrIdCard).success.value
          .set(LiveInTheUkYesNoPage, false).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value

        val result = helper(userAnswers, isProvisional, name.displayFullName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.dateOfBirth.checkYourAnswersLabel", displayName)), answer = Html("9 March 1996"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", displayName)), answer = Html("No"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.passportOrIdCardDetails.checkYourAnswersLabel", displayName)), answer = Html("France<br />123<br />3 February 2022"), changeUrl = amendRts.PassportOrIdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individual.liveInTheUkYesNo.checkYourAnswersLabel", displayName)), answer = Html("No"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.nonUkAddress.checkYourAnswersLabel", displayName)), answer = Html("value 1<br />value 2<br />France"), changeUrl = rts.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("individual.telephoneNumber.checkYourAnswersLabel", displayName)), answer = Html("999"), changeUrl = rts.TelephoneNumberController.onPageLoad(mode).url)
          )
        )
      }
    }
  }
}
