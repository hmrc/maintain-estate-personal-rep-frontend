/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import models.IndividualOrBusiness.Individual
import models.{CombinedPassportOrIdCard, IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, PassportOrIdCard, UkAddress}
import pages.IndividualOrBusinessPage
import pages.individual._
import pages.individual.add._
import pages.individual.amend.PassportOrIdCardDetailsPage

class IndividualMapperSpec extends SpecBase {

  private val name: Name = Name("John", None, "Doe")
  private val nino = "AA000000A"
  private val dateOfBirth = LocalDate.parse("1996-03-09")
  private val startDate = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val telephoneNumber: String = "999"
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")
  private val passport: Passport = Passport("FR", "123", LocalDate.parse("2022-02-03"))
  private val idCard: IdCard = IdCard("FR", "123", LocalDate.parse("2022-02-03"))
  private val passportOrIdCard: CombinedPassportOrIdCard = CombinedPassportOrIdCard("FR", "123", LocalDate.parse("2022-02-03"))
  private val email: String = "email@example.com"

  "Individual mapper" when {

    val mapper = injector.instanceOf[IndividualMapper]

    val baseAnswers = emptyUserAnswers
      .set(IndividualOrBusinessPage, Individual).success.value
      .set(NamePage, name).success.value
      .set(DateOfBirthPage, dateOfBirth).success.value
      .set(TelephoneNumberPage, telephoneNumber).success.value
      .set(StartDatePage, startDate).success.value

    "generate individual personal rep model with NINO, UK address and an email" in {

      val userAnswers = baseAnswers
        .set(NationalInsuranceNumberYesNoPage, true).success.value
        .set(NationalInsuranceNumberPage, nino).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(EmailAddressYesNoPage, true).success.value
        .set(EmailAddressPage, email).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe dateOfBirth
      result.identification mustBe NationalInsuranceNumber(nino)
      result.address mustBe ukAddress
      result.phoneNumber mustBe telephoneNumber
      result.email mustBe Some(email)
      result.entityStart mustBe startDate
    }

    "generate individual personal rep model with passport and UK address" in {

      val userAnswers = baseAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(PassportOrIdCardPage, PassportOrIdCard.Passport).success.value
        .set(PassportDetailsPage, passport).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(EmailAddressYesNoPage, false).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe dateOfBirth
      result.identification mustBe passport
      result.address mustBe ukAddress
      result.phoneNumber mustBe telephoneNumber
      result.email mustBe None
      result.entityStart mustBe startDate
    }

    "generate individual personal rep model with ID card and non-UK address" in {

      val userAnswers = baseAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(PassportOrIdCardPage, PassportOrIdCard.IdCard).success.value
        .set(IdCardDetailsPage, idCard).success.value
        .set(LiveInTheUkYesNoPage, false).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value
        .set(EmailAddressYesNoPage, false).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe dateOfBirth
      result.identification mustBe idCard
      result.address mustBe nonUkAddress
      result.phoneNumber mustBe telephoneNumber
      result.email mustBe None
      result.entityStart mustBe startDate
    }

    "generate individual personal rep model with combined passport and ID card and UK address" in {

      val userAnswers = baseAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(PassportOrIdCardDetailsPage, passportOrIdCard).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(EmailAddressYesNoPage, false).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe dateOfBirth
      result.identification mustBe passportOrIdCard
      result.address mustBe ukAddress
      result.phoneNumber mustBe telephoneNumber
      result.email mustBe None
      result.entityStart mustBe startDate
    }
  }
}
