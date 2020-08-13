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

package utils.extractors

import models.IndividualOrBusiness.Business
import models.{Address, BusinessPersonalRep, NonUkAddress, UkAddress, UserAnswers}
import pages.IndividualOrBusinessPage
import pages.business._
import pages.business.add.StartDatePage

import scala.util.Try

class BusinessExtractor {

  def apply(answers: UserAnswers, business: BusinessPersonalRep): Try[UserAnswers] =
    answers.deleteAtPath(pages.business.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Business))
      .flatMap(_.set(NamePage, business.name))
      .flatMap(_.set(TelephoneNumberPage, business.phoneNumber))
      .flatMap(answers => extractAddress(business.address, answers))
      .flatMap(answers => extractUtr(business.utr, answers))
      .flatMap(answers => extractEmailAddress(business.email, answers))
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

  private def extractEmailAddress(emailAddress: Option[String], userAnswers: UserAnswers): Try[UserAnswers] = {
    emailAddress match {
      case Some(email) =>
        userAnswers.set(EmailAddressYesNoPage, true)
          .flatMap(_.set(EmailAddressPage, email))
      case None =>
        userAnswers.set(EmailAddressYesNoPage, false)
    }
  }
}
