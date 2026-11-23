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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

sealed trait Address

case class UkAddress(line1: String, line2: String, line3: Option[String], line4: Option[String], postcode: String)
    extends Address

object UkAddress {

  given reads: Reads[UkAddress] =
    ((__ \ Symbol("line1")).read[String] and
      (__ \ Symbol("line2")).read[String] and
      (__ \ Symbol("line3")).readNullable[String] and
      (__ \ Symbol("line4")).readNullable[String] and
      (__ \ Symbol("postCode")).read[String]).apply(UkAddress.apply)

  given writes: Writes[UkAddress] =
    ((__ \ Symbol("line1")).write[String] and
      (__ \ Symbol("line2")).write[String] and
      (__ \ Symbol("line3")).writeNullable[String] and
      (__ \ Symbol("line4")).writeNullable[String] and
      (__ \ Symbol("postCode")).write[String] and
      (__ \ Symbol("country")).write[String]).apply(address =>
      (
        address.line1,
        address.line2,
        address.line3,
        address.line4,
        address.postcode,
        "GB"
      )
    )

  given format: Format[UkAddress] = Format[UkAddress](reads, writes)
}

case class NonUkAddress(line1: String, line2: String, line3: Option[String] = None, country: String) extends Address

object NonUkAddress {
  given format: OFormat[NonUkAddress] = Json.format[NonUkAddress]
}

object Address {

  given reads: Reads[Address] =
    __.read[UkAddress](using UkAddress.reads).widen[Address] orElse
      __.read[NonUkAddress](using NonUkAddress.format).widen[Address]

  given writes: Writes[Address] = Writes {
    case a: UkAddress    => Json.toJson(a)(using UkAddress.writes)
    case a: NonUkAddress => Json.toJson(a)(using NonUkAddress.format)
  }

}
