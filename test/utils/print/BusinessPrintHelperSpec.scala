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

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{NonUkAddress, NormalMode, UkAddress}
import pages.IndividualOrBusinessPage
import pages.business._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BusinessPrintHelperSpec extends SpecBase {

  private val name: String = "Name"
  private val utr: String = "1234567890"
  private val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  private val telephoneNumber: String = "999"
  private val startDate: LocalDate = LocalDate.parse("2020-01-01")

  "Business print helper" must {

    val helper = injector.instanceOf[BusinessPrintHelper]

    "generate add business personal rep section" when {

      val mode = NormalMode

      val baseAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Business).success.value
        .set(NamePage, name).success.value
        .set(TelephoneNumberPage, telephoneNumber).success.value
        .set(StartDatePage, startDate).success.value

      "UK registered company with UK address" in {

        val userAnswers = baseAnswers
          .set(UkRegisteredCompanyYesNoPage, true).success.value
          .set(UtrPage, utr).success.value
          .set(AddressUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value

        val result = helper(userAnswers, provisional = true, name)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Business"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.ukRegisteredCompanyYesNo.checkYourAnswersLabel")), answer = Html("Yes"), changeUrl = controllers.business.routes.UkRegisteredCompanyYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.ukCompany.name.checkYourAnswersLabel")), answer = Html("Name"), changeUrl = controllers.business.routes.UkCompanyNameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.utr.checkYourAnswersLabel", name)), answer = Html("1234567890"), changeUrl = controllers.business.routes.UtrController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = controllers.business.routes.AddressUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.ukAddress.checkYourAnswersLabel", name)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.business.routes.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.telephoneNumber.checkYourAnswersLabel", name)), answer = Html("999"), changeUrl = controllers.business.routes.TelephoneNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.startDate.checkYourAnswersLabel", name)), answer = Html("1 January 2020"), changeUrl = controllers.business.add.routes.StartDateController.onPageLoad().url)
          )
        )
      }

      "Non-UK registered company with Non-UK address" in {

        val userAnswers = baseAnswers
          .set(UkRegisteredCompanyYesNoPage, false).success.value
          .set(AddressUkYesNoPage, false).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value

        val result = helper(userAnswers, provisional = true, name)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualOrBusiness.checkYourAnswersLabel")), answer = Html("Business"), changeUrl = controllers.routes.IndividualOrBusinessController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.ukRegisteredCompanyYesNo.checkYourAnswersLabel")), answer = Html("No"), changeUrl = controllers.business.routes.UkRegisteredCompanyYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.nonUkCompany.name.checkYourAnswersLabel")), answer = Html("Name"), changeUrl = controllers.business.routes.NonUkCompanyNameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = controllers.business.routes.AddressUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.business.routes.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.telephoneNumber.checkYourAnswersLabel", name)), answer = Html("999"), changeUrl = controllers.business.routes.TelephoneNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("business.startDate.checkYourAnswersLabel", name)), answer = Html("1 January 2020"), changeUrl = controllers.business.add.routes.StartDateController.onPageLoad().url)
          )
        )
      }
    }
  }
}
