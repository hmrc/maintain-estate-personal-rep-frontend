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

package utils.extractors

import models.IndividualOrBusiness.Individual
import models._
import pages.IndividualOrBusinessPage
import pages.individual._
import pages.individual.add._
import pages.individual.amend.PassportOrIdCardDetailsPage

import scala.util.Try

class IndividualExtractor {

  def apply(answers: UserAnswers, individual: IndividualPersonalRep): Try[UserAnswers] =
    answers.deleteAtPath(pages.individual.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Individual))
      .flatMap(_.set(NamePage, individual.name))
      .flatMap(_.set(TelephoneNumberPage, individual.phoneNumber))
      .flatMap(_.set(DateOfBirthPage, individual.dateOfBirth))
      .flatMap(answers => extractAddress(individual.address, answers))
      .flatMap(answers => extractIdentification(individual, answers))
      .flatMap(answers => extractEmailAddress(individual.email, answers))
      .flatMap(_.set(StartDatePage, individual.entityStart))

  private def extractAddress(address: Address, answers: UserAnswers) : Try[UserAnswers] = {
    address match {
      case uk: UkAddress =>
        answers.set(LiveInTheUkYesNoPage, true)
          .flatMap(_.set(UkAddressPage, uk))
      case nonUk: NonUkAddress =>
        answers.set(LiveInTheUkYesNoPage, false)
          .flatMap(_.set(NonUkAddressPage, nonUk))
    }
  }

  private def extractIdentification(individual: IndividualPersonalRep,
                                    answers: UserAnswers) : Try[UserAnswers] = {

    individual.identification match {
      case NationalInsuranceNumber(nino) =>
        answers.set(NationalInsuranceNumberYesNoPage, true)
          .flatMap(_.set(NationalInsuranceNumberPage, nino))
      case p: Passport =>
        answers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(PassportOrIdCardPage, PassportOrIdCard.Passport))
          .flatMap(_.set(PassportDetailsPage, p))
      case id: IdCard =>
        answers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(PassportOrIdCardPage, PassportOrIdCard.IdCard))
          .flatMap(_.set(IdCardDetailsPage, id))
      case combined: CombinedPassportOrIdCard =>
        answers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(PassportOrIdCardDetailsPage, combined))
      case _ =>
        answers.set(NationalInsuranceNumberYesNoPage, false)
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
