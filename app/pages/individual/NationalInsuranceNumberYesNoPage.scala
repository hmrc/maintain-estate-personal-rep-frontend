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

package pages.individual

import models.UserAnswers
import pages.QuestionPage
import pages.individual.add.{IdCardDetailsPage, PassportDetailsPage}
import play.api.libs.json.JsPath

import scala.util.Try

case object NationalInsuranceNumberYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = basePath \ toString

  override def toString: String = "nationalInsuranceNumberYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(true) =>
        userAnswers.remove(PassportOrIdCardDetailsYesNoPage)
          .flatMap(_.remove(PassportDetailsPage))
          .flatMap(_.remove(IdCardDetailsPage))
//          .flatMap(_.remove(LiveInTheUkYesNoPage)) // TODO Check if need to remove
//          .flatMap(_.remove(UkAddressPage)) // TODO Check if need to remove
//          .flatMap(_.remove(NonUkAddressPage)) // TODO Check if need to remove
      case Some(false) =>
        userAnswers.remove(NationalInsuranceNumberPage)
//          .flatMap(_.remove(LiveInTheUkYesNoPage)) // TODO Check if need to remove
//          .flatMap(_.remove(UkAddressPage)) // TODO Check if need to remove
//          .flatMap(_.remove(NonUkAddressPage)) // TODO Check if need to remove
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
