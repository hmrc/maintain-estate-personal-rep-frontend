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
    ((__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).read[LocalDate] and
      __.lazyRead(readAtSubPath[IndividualIdentification](__ \ 'identification)) and
      __.lazyRead(readAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'phoneNumber).read[String] and
      (__ \ 'email).readNullable[String] and
      (__ \ "entityStart").read[LocalDate]).tupled.map{

      case (name, dob, nino, identification, phoneNumber, email, entityStart) =>
        IndividualPersonalRep(name, dob, nino, identification, phoneNumber, email, entityStart)

    }

  implicit val writes: Writes[IndividualPersonalRep] =
    ((__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).write[LocalDate] and
      (__ \ 'identification).write[IndividualIdentification] and
      (__ \ 'identification \ 'address).write[Address] and
      (__ \ 'phoneNumber).write[String] and
      (__ \ 'email).writeNullable[String] and
      (__ \ "entityStart").write[LocalDate]
      ).apply(unlift(IndividualPersonalRep.unapply))

}
