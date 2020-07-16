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

import javax.inject.Inject
import models.{Mode, UserAnswers}
import pages.Page
import pages.business._
import play.api.mvc.Call

class IndividualNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case UtrPage => ???
    case UkAddressPage => ???
    case NonUkAddressPage => ???
    case TelephoneNumberPage => ???

  }

  private def conditionalNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case UkRegisteredCompanyYesNoPage => ua =>
      yesNoNav(ua, UkRegisteredCompanyYesNoPage, ???, ???)
    case NamePage => ua =>
      yesNoNav(ua, UkRegisteredCompanyYesNoPage, ???, ???)
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, ???, ???)
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(mode)
}
