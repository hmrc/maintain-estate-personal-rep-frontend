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
import controllers.individual.add.{routes => addRts}
import controllers.individual.amend.{routes => amendRts}
import controllers.individual.{routes => rts}
import models.{CheckMode, NormalMode, UserAnswers}
import pages.IndividualOrBusinessPage
import pages.individual._
import pages.individual.add._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class IndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                      countryOptions: CountryOptions) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    val add: Seq[AnswerRow] = Seq(
      bound.enumQuestion(IndividualOrBusinessPage, "individualOrBusiness", controllers.routes.IndividualOrBusinessController.onPageLoad(NormalMode).url),
      bound.nameQuestion(NamePage, "individual.name", rts.NameController.onPageLoad(NormalMode).url),
      bound.dateQuestion(DateOfBirthPage, "individual.dateOfBirth", rts.DateOfBirthController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individual.nationalInsuranceNumberYesNo", rts.NationalInsuranceNumberYesNoController.onPageLoad(NormalMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individual.nationalInsuranceNumber", rts.NationalInsuranceNumberController.onPageLoad(NormalMode).url),
      bound.enumQuestion(PassportOrIdCardPage, "passportOrIdCard", rts.PassportOrIdCardController.onPageLoad(NormalMode).url),
      bound.passportDetailsQuestion(PassportDetailsPage, "individual.passportDetails", addRts.PassportDetailsController.onPageLoad().url),
      bound.idCardDetailsQuestion(IdCardDetailsPage, "individual.idCardDetails", addRts.IdCardDetailsController.onPageLoad().url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individual.liveInTheUkYesNo", rts.LiveInTheUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "individual.ukAddress", rts.UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "individual.nonUkAddress", rts.NonUkAddressController.onPageLoad(NormalMode).url),
      bound.stringQuestion(TelephoneNumberPage, "individual.telephoneNumber", rts.TelephoneNumberController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "individual.startDate", addRts.StartDateController.onPageLoad().url)
    ).flatten

    val amend: Seq[AnswerRow] = Seq(
      bound.enumQuestion(IndividualOrBusinessPage, "individualOrBusiness", controllers.routes.IndividualOrBusinessController.onPageLoad(CheckMode).url),
      bound.nameQuestion(NamePage, "individual.name", rts.NameController.onPageLoad(CheckMode).url),
      bound.dateQuestion(DateOfBirthPage, "individual.dateOfBirth", rts.DateOfBirthController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individual.nationalInsuranceNumberYesNo", rts.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individual.nationalInsuranceNumber", rts.NationalInsuranceNumberController.onPageLoad(CheckMode).url),
      bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individual.passportOrIdCardDetails", amendRts.PassportOrIdCardDetailsController.onPageLoad().url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individual.liveInTheUkYesNo", rts.LiveInTheUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "individual.ukAddress", rts.UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "individual.nonUkAddress", rts.NonUkAddressController.onPageLoad(CheckMode).url),
      bound.stringQuestion(TelephoneNumberPage, "individual.telephoneNumber", rts.TelephoneNumberController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
