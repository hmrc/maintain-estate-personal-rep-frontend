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

package utils.print

import com.google.inject.Inject
import controllers.business.{routes => rts}
import controllers.business.add.{routes => addRts}
import models.{CheckMode, NormalMode, UserAnswers}
import pages.IndividualOrBusinessPage
import pages.business._
import pages.business.add.StartDatePage
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class BusinessPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, name)

    val add: Seq[AnswerRow] = Seq(
      bound.enumQuestion(IndividualOrBusinessPage, "individualOrBusiness", controllers.routes.IndividualOrBusinessController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(UkRegisteredCompanyYesNoPage, "business.ukRegisteredCompanyYesNo", rts.UkRegisteredCompanyYesNoController.onPageLoad(NormalMode).url),
      bound.conditionalStringQuestion(
        NamePage,
        UkRegisteredCompanyYesNoPage,
        ("business.ukCompanyName", "business.nonUkCompanyName"),
        (rts.UkCompanyNameController.onPageLoad(NormalMode).url, rts.NonUkCompanyNameController.onPageLoad(NormalMode).url)
      ),
      bound.stringQuestion(UtrPage, "business.utr", rts.UtrController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "business.addressUkYesNo", rts.AddressUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "business.ukAddress", rts.UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "business.nonUkAddress", rts.NonUkAddressController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(EmailAddressYesNoPage, "business.emailYesNo", rts.EmailAddressYesNoController.onPageLoad(NormalMode).url),
      bound.stringQuestion(EmailAddressPage, "business.email", rts.EmailAddressController.onPageLoad(NormalMode).url),
      bound.stringQuestion(TelephoneNumberPage, "business.telephoneNumber", rts.TelephoneNumberController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "business.startDate", addRts.StartDateController.onPageLoad().url)
    ).flatten

    val amend: Seq[AnswerRow] = Seq(
      bound.enumQuestion(IndividualOrBusinessPage, "individualOrBusiness", controllers.routes.IndividualOrBusinessController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(UkRegisteredCompanyYesNoPage, "business.ukRegisteredCompanyYesNo", rts.UkRegisteredCompanyYesNoController.onPageLoad(CheckMode).url),
      bound.conditionalStringQuestion(
        NamePage,
        UkRegisteredCompanyYesNoPage,
        ("business.ukCompanyName", "business.nonUkCompanyName"),
        (rts.UkCompanyNameController.onPageLoad(CheckMode).url, rts.NonUkCompanyNameController.onPageLoad(CheckMode).url)
      ),
      bound.stringQuestion(UtrPage, "business.utr", rts.UtrController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "business.addressUkYesNo", rts.AddressUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "business.ukAddress", rts.UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "business.nonUkAddress", rts.NonUkAddressController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(EmailAddressYesNoPage, "business.emailYesNo", rts.EmailAddressYesNoController.onPageLoad(CheckMode).url),
      bound.stringQuestion(EmailAddressPage, "business.email", rts.EmailAddressController.onPageLoad(CheckMode).url),
      bound.stringQuestion(TelephoneNumberPage, "business.telephoneNumber", rts.TelephoneNumberController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
