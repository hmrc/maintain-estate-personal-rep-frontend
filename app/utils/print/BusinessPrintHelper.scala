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

import com.google.inject.Inject
import controllers.business.{routes => rts}
import controllers.business.add.{routes => addRts}
import models.{NormalMode, UserAnswers}
import pages.IndividualOrBusinessPage
import pages.business._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class BusinessPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                    countryOptions: CountryOptions) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    val ukRegPrefix = userAnswers.get(UkRegisteredCompanyYesNoPage).map(if (_) {"uk"} else {"nonUk"}).getOrElse("uk")

    val companyNameChangeRoute = userAnswers.get(UkRegisteredCompanyYesNoPage).map{
      if (_) {rts.UkCompanyNameController.onPageLoad(NormalMode).url
      } else {
        rts.NonUkCompanyNameController.onPageLoad(NormalMode).url
      }
    }.getOrElse(rts.UkCompanyNameController.onPageLoad(NormalMode).url)

    val add: Seq[AnswerRow] = Seq(
      bound.enumQuestion(IndividualOrBusinessPage, "individualOrBusiness", controllers.routes.IndividualOrBusinessController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(UkRegisteredCompanyYesNoPage, "business.ukRegisteredCompanyYesNo", rts.UkRegisteredCompanyYesNoController.onPageLoad(NormalMode).url),
      bound.stringQuestion(NamePage, s"business.${ukRegPrefix}CompanyName", companyNameChangeRoute),
      bound.stringQuestion(UtrPage, "business.utr", rts.UtrController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "business.addressUkYesNo", rts.AddressUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "business.ukAddress", rts.UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "business.nonUkAddress", rts.NonUkAddressController.onPageLoad(NormalMode).url),
      bound.stringQuestion(TelephoneNumberPage, "business.telephoneNumber", rts.TelephoneNumberController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "business.startDate", addRts.StartDateController.onPageLoad().url)
    ).flatten

    AnswerSection(
      None,
      add
    )
  }
}
