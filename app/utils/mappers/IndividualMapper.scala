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

package utils.mappers

import java.time.LocalDate

import models._
import org.slf4j.LoggerFactory
import pages.individual._
import pages.individual.add._
import pages.individual.amend.PassportOrIdCardDetailsPage
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class IndividualMapper {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def apply(answers: UserAnswers): Option[IndividualPersonalRep] = {
    val readFromUserAnswers: Reads[IndividualPersonalRep] =
      (
        NamePage.path.read[Name] and
          DateOfBirthPage.path.read[LocalDate] and
          readIdentification and
          readAddress and
          TelephoneNumberPage.path.read[String] and
          readEmailAddress and
          StartDatePage.path.read[LocalDate]
        ) (IndividualPersonalRep.apply _)

    answers.data.validate[IndividualPersonalRep](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"Failed to rehydrate IndividualPersonalRep from UserAnswers due to $errors")
        None
    }
  }

  private def readIdentification: Reads[IndividualIdentification] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap[IndividualIdentification] {
      case true => NationalInsuranceNumberPage.path.read[String].map(NationalInsuranceNumber(_))
      case false => readPassportOrIdCard
    }
  }

  private def readPassportOrIdCard: Reads[IndividualIdentification] = {
    PassportOrIdCardPage.path.readNullable[PassportOrIdCard].flatMap[IndividualIdentification] {
      case Some(PassportOrIdCard.Passport) => PassportDetailsPage.path.read[Passport].widen[IndividualIdentification]
      case Some(PassportOrIdCard.IdCard) => IdCardDetailsPage.path.read[IdCard].widen[IndividualIdentification]
      case _ => PassportOrIdCardDetailsPage.path.read[CombinedPassportOrIdCard].widen[IndividualIdentification]
    }
  }

  private def readAddress: Reads[Address] = {
    LiveInTheUkYesNoPage.path.read[Boolean].flatMap[Address] {
      case true => UkAddressPage.path.read[UkAddress].widen[Address]
      case false => NonUkAddressPage.path.read[NonUkAddress].widen[Address]
    }
  }

  private def readEmailAddress: Reads[Option[String]] = {
    EmailAddressYesNoPage.path.read[Boolean].flatMap[Option[String]] {
      case true => EmailAddressPage.path.read[String].map(Some(_))
      case false => Reads(_ => JsSuccess(None))
    }
  }

}
