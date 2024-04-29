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

package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class IndividualPersonalRep(name: Name,
                                       dateOfBirth: LocalDate,
                                       identification: IndividualIdentification,
                                       address : Address,
                                       phoneNumber: String,
                                       email: Option[String],
                                       entityStart: LocalDate) extends PersonalRep

object IndividualPersonalRep extends EntityReads {

  implicit val reads: Reads[IndividualPersonalRep] =
    ((__ \ Symbol("name")).read[Name] and
      (__ \ Symbol("dateOfBirth")).read[LocalDate] and
      __.lazyRead(readAtSubPath[IndividualIdentification](__ \ Symbol("identification"))) and
      __.lazyRead(readAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      (__ \ Symbol("phoneNumber")).read[String] and
      (__ \ Symbol("email")).readNullable[String] and
      (__ \ "entityStart").read[LocalDate]).tupled.map{

      case (name, dob, nino, identification, phoneNumber, email, entityStart) =>
        IndividualPersonalRep(name, dob, nino, identification, phoneNumber, email, entityStart)

    }

  implicit val writes: Writes[IndividualPersonalRep] =
    ((__ \ Symbol("name")).write[Name] and
      (__ \ Symbol("dateOfBirth")).write[LocalDate] and
      (__ \ Symbol("identification")).write[IndividualIdentification] and
      (__ \ Symbol("identification") \ Symbol("address")).write[Address] and
      (__ \ Symbol("phoneNumber")).write[String] and
      (__ \ Symbol("email")).writeNullable[String] and
      (__ \ "entityStart").write[LocalDate]
      ).apply(unlift(IndividualPersonalRep.unapply))

}
