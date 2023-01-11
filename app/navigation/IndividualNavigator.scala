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

package navigation

import controllers.individual.add.{routes => addRts}
import controllers.individual.amend.{routes => amendRts}
import controllers.individual.{routes => rts}
import javax.inject.Inject
import models.PassportOrIdCard._
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.individual._
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage, PassportOrIdCardPage, StartDatePage}
import pages.individual.amend.PassportOrIdCardDetailsPage
import play.api.mvc.Call

class IndividualNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case NamePage => rts.DateOfBirthController.onPageLoad(mode)
    case DateOfBirthPage => rts.NationalInsuranceNumberYesNoController.onPageLoad(mode)
    case NationalInsuranceNumberPage | IdCardDetailsPage | PassportDetailsPage | PassportOrIdCardDetailsPage =>
      rts.LiveInTheUkYesNoController.onPageLoad(mode)
    case UkAddressPage | NonUkAddressPage => rts.EmailAddressYesNoController.onPageLoad(mode)
    case EmailAddressPage => rts.TelephoneNumberController.onPageLoad(mode)
    case TelephoneNumberPage => telephoneNumberRoute(mode)
    case StartDatePage => addRts.CheckDetailsController.onPageLoad()
  }

  private def conditionalNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(mode), combinedOrSeparateDetailsRoute(mode))
    case PassportOrIdCardPage => ua => passportOrIdCardDetailsRoute(ua)
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
    case EmailAddressYesNoPage => ua =>
      yesNoNav(ua, EmailAddressYesNoPage, rts.EmailAddressController.onPageLoad(mode), rts.TelephoneNumberController.onPageLoad(mode))
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(mode)

  private def passportOrIdCardDetailsRoute(ua: UserAnswers): Call =
    ua.get(PassportOrIdCardPage) match {
      case Some(IdCard) => addRts.IdCardDetailsController.onPageLoad()
      case Some(Passport) => addRts.PassportDetailsController.onPageLoad()
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }

  private def combinedOrSeparateDetailsRoute(mode: Mode): Call =
    mode match {
      case NormalMode => addRts.PassportOrIdCardController.onPageLoad()
      case CheckMode => amendRts.PassportOrIdCardDetailsController.onPageLoad()
    }

  private def telephoneNumberRoute(mode: Mode): Call =
    mode match {
      case NormalMode => addRts.StartDateController.onPageLoad()
      case CheckMode => amendRts.CheckDetailsController.renderFromUserAnswers()
    }

}
