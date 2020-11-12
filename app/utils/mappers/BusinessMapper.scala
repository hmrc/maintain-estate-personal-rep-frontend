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

import models.{Address, BusinessPersonalRep, NonUkAddress, UkAddress, UserAnswers}
import pages.business._
import pages.business.add.StartDatePage
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class BusinessMapper extends Logging {

  def apply(answers: UserAnswers): Option[BusinessPersonalRep] = {
    val readFromUserAnswers: Reads[BusinessPersonalRep] =
      (
        NamePage.path.read[String] and
          TelephoneNumberPage.path.read[String] and
          UtrPage.path.readNullable[String] and
          readAddress and
          readEmailAddress and
          StartDatePage.path.read[LocalDate]
        ) (BusinessPersonalRep.apply _)

    answers.data.validate[BusinessPersonalRep](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"Failed to rehydrate BusinessPersonalRep from UserAnswers due to $errors")
        None
    }
  }

  private def readAddress: Reads[Address] = {
    AddressUkYesNoPage.path.read[Boolean].flatMap[Address] {
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
