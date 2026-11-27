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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

final case class IndividualPersonalRep(
  name: Name,
  dateOfBirth: LocalDate,
  identification: IndividualIdentification,
  address: Address,
  phoneNumber: String,
  email: Option[String],
  entityStart: LocalDate
) extends PersonalRep

object IndividualPersonalRep extends EntityReads {

  given reads: Reads[IndividualPersonalRep] =
    ((__ \ Symbol("name")).read[Name] and
      (__ \ Symbol("dateOfBirth")).read[LocalDate] and
      __.lazyRead(readAtSubPath[IndividualIdentification](__ \ Symbol("identification"))) and
      __.lazyRead(readAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      (__ \ Symbol("phoneNumber")).read[String] and
      (__ \ Symbol("email")).readNullable[String] and
      (__ \ "entityStart").read[LocalDate]).tupled.map {

      case (name, dob, nino, identification, phoneNumber, email, entityStart) =>
        IndividualPersonalRep(name, dob, nino, identification, phoneNumber, email, entityStart)

    }

  given writes: Writes[IndividualPersonalRep] =
    ((__ \ "name").write[Name] and
      (__ \ "dateOfBirth").write[LocalDate] and
      (__ \ "identification").write[IndividualIdentification] and
      (__ \ "identification" \ "address").write[Address] and
      (__ \ "phoneNumber").write[String] and
      (__ \ "email").writeNullable[String] and
      (__ \ "entityStart").write[LocalDate])(rep =>
      (rep.name, rep.dateOfBirth, rep.identification, rep.address, rep.phoneNumber, rep.email, rep.entityStart)
    )

}
