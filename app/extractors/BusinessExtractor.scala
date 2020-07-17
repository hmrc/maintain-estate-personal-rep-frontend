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

package extractors

import com.google.inject.Inject
import models.{Address, BusinessPersonalRep, NonUkAddress, UkAddress, UserAnswers}
import pages.business._

import scala.util.Try

class BusinessExtractor @Inject()() {

  def apply(answers: UserAnswers, business : BusinessPersonalRep): Try[UserAnswers] =
    answers.deleteAtPath(pages.business.basePath)
      .flatMap(_.set(NamePage, business.name))
      .flatMap(_.set(TelephoneNumberPage, business.phoneNumber))
      .flatMap(answers => extractAddress(business.address, answers))
      .flatMap(answers => extractUtr(business.utr, answers))
      .flatMap(_.set(StartDatePage, business.entityStart))

  private def extractUtr(utr: Option[String], answers: UserAnswers) : Try[UserAnswers] = {
    utr match {
      case Some(utr) =>
        answers.set(UkRegisteredCompanyYesNoPage, true)
        .flatMap(_.set(UtrPage, utr))
      case _ => answers.set(UkRegisteredCompanyYesNoPage, false)
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers) : Try[UserAnswers] = {
    address match {
      case uk: UkAddress =>
        answers.set(AddressUkYesNoPage, true)
          .flatMap(_.set(UkAddressPage, uk))
      case nonUk: NonUkAddress =>
        answers.set(AddressUkYesNoPage, false)
          .flatMap(_.set(NonUkAddressPage, nonUk))
    }
  }
}
