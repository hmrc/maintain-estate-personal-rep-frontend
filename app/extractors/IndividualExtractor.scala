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
import models.{Address, CombinedPassportOrIdCard, IdCard, IndividualPersonalRep, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import pages.individual._

import scala.util.Try

class IndividualExtractor @Inject()() {

  def apply(answers: UserAnswers, individual : IndividualPersonalRep): Try[UserAnswers] =
    answers.deleteAtPath(pages.individual.basePath)
      .flatMap(_.set(NamePage, individual.name))
      .flatMap(_.set(TelephoneNumberPage, individual.phoneNumber))
      .flatMap(_.set(DateOfBirthPage, individual.dateOfBirth))
      .flatMap(answers => extractAddress(individual.address, answers))
      .flatMap(answers => extractIdentification(individual, answers))
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
                                    answers: UserAnswers) : Try[UserAnswers] =
  {
    ??? ///TODO: Fix Identification extraction
//    individual.identification match {
//      case Some(NationalInsuranceNumber(nino)) =>
//        answers.set(NationalInsuranceNumberYesNoPage, true)
//          .flatMap(_.set(NationalInsuranceNumberPage, nino))
//      case Some(p : Passport) =>
//        answers.set(NationalInsuranceNumberYesNoPage, false)
//          .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, true))
//          .flatMap(_.set(PassportDetailsPage, p))
//      case Some(id: IdCard) =>
//        answers.set(NationalInsuranceNumberYesNoPage, false)
//          .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, false))
//          .flatMap(_.set(IdCardDetailsPage, id))
//      case Some(combined: CombinedPassportOrIdCard) =>
//        answers.set(NationalInsuranceNumberYesNoPage, false)
//          .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, true))
//          .flatMap(_.set(PassportOrIdCardDetailsPage, combined))
//      case _ =>
//        answers.set(NationalInsuranceNumberYesNoPage, false)
//    }
  }
}
