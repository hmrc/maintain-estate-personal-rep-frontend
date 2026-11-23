/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json, Reads, Writes, __}

sealed trait IndividualIdentification

object IndividualIdentification {

  given reads: Reads[IndividualIdentification] =
    (__ \ Symbol("passport")).read[CombinedPassportOrIdCard].widen[IndividualIdentification] orElse
      __.read[NationalInsuranceNumber].widen[IndividualIdentification]

  given writes: Writes[IndividualIdentification] = Writes {
    case ni: NationalInsuranceNumber => Json.toJson(ni)(using NationalInsuranceNumber.format)
    case p: Passport                 => Json.obj("passport" -> Json.toJson(p)(using Passport.format))
    case i: IdCard                   => Json.obj("passport" -> Json.toJson(i)(using IdCard.format))
    case c: CombinedPassportOrIdCard => Json.obj("passport" -> Json.toJson(c)(using CombinedPassportOrIdCard.format))
  }

}

case class NationalInsuranceNumber(nino: String) extends IndividualIdentification

object NationalInsuranceNumber {
  given format: Format[NationalInsuranceNumber] = Json.format[NationalInsuranceNumber]
}

case class Passport(countryOfIssue: String, number: String, expirationDate: LocalDate)
    extends IndividualIdentification {
  def asCombined: CombinedPassportOrIdCard = CombinedPassportOrIdCard(countryOfIssue, number, expirationDate)
}

object Passport {
  given format: Format[Passport] = Json.format[Passport]
}

case class IdCard(countryOfIssue: String, number: String, expirationDate: LocalDate) extends IndividualIdentification {
  def asCombined: CombinedPassportOrIdCard = CombinedPassportOrIdCard(countryOfIssue, number, expirationDate)
}

object IdCard {
  given format: Format[IdCard] = Json.format[IdCard]
}

case class CombinedPassportOrIdCard(countryOfIssue: String, number: String, expirationDate: LocalDate)
    extends IndividualIdentification

object CombinedPassportOrIdCard {
  given format: Format[CombinedPassportOrIdCard] = Json.format[CombinedPassportOrIdCard]
}
