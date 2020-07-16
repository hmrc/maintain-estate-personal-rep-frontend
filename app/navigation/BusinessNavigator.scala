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

import controllers.business.{routes => rts}
import controllers.business.add.{routes => addRts}
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.business._
import play.api.mvc.Call

class BusinessNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case UtrPage => rts.AddressUkYesNoController.onPageLoad(mode)
    case UkAddressPage | NonUkAddressPage => rts.TelephoneNumberController.onPageLoad(mode)
    case TelephoneNumberPage => telephoneNumberRoute(mode)
    case StartDatePage => addRts.CheckDetailsController.onPageLoad()
  }

  private def conditionalNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case UkRegisteredCompanyYesNoPage => ua =>
      yesNoNav(ua, UkRegisteredCompanyYesNoPage, rts.UkCompanyNameController.onPageLoad(mode), rts.NonUkCompanyNameController.onPageLoad(mode))
    case NamePage => ua =>
      yesNoNav(ua, UkRegisteredCompanyYesNoPage, rts.UtrController.onPageLoad(mode), rts.AddressUkYesNoController.onPageLoad(mode))
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(mode)

  private def telephoneNumberRoute(mode: Mode): Call = {
    mode match {
      case NormalMode => addRts.StartDateController.onPageLoad()
      case CheckMode => ???
    }
  }
}
