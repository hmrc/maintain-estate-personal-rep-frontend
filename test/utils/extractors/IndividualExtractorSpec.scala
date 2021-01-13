/*
 * Copyright 2021 HM Revenue & Customs
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

package utils.extractors

import java.time.LocalDate

import base.SpecBase
import models.IndividualOrBusiness.Individual
import models.{CombinedPassportOrIdCard, IndividualPersonalRep, Name, NationalInsuranceNumber, NonUkAddress, UkAddress}
import pages.IndividualOrBusinessPage
import pages.individual._
import pages.individual.add.StartDatePage
import pages.individual.amend.PassportOrIdCardDetailsPage

class IndividualExtractorSpec extends SpecBase {

  private val name: Name = Name("First", None, "Last")
  private val dateOfBirth: LocalDate = LocalDate.parse("1996-02-03")
  private val nino: String = "AA000000A"
  private val passportOrIdCard: CombinedPassportOrIdCard = CombinedPassportOrIdCard("FR", "123", LocalDate.parse("2022-04-05"))
  private val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  private val telephoneNumber: String = "999"
  private val startDate: LocalDate = LocalDate.parse("2020-01-01")
  private val email: String = "email@example.com"

  "Individual extractor" must {

    val extractor = injector.instanceOf[IndividualExtractor]

    "populate user answers" when {

      "rep has NINO, UK address and an email" in {

        val personalRep: IndividualPersonalRep = IndividualPersonalRep(
          name = name,
          dateOfBirth = dateOfBirth,
          identification = NationalInsuranceNumber(nino),
          address = ukAddress,
          phoneNumber = telephoneNumber,
          email = Some(email),
          entityStart = startDate
        )

        val result = extractor(emptyUserAnswers, personalRep).get

        result.get(IndividualOrBusinessPage).get mustEqual Individual
        result.get(NamePage).get mustEqual name
        result.get(DateOfBirthPage).get mustEqual dateOfBirth
        result.get(NationalInsuranceNumberYesNoPage).get mustEqual true
        result.get(NationalInsuranceNumberPage).get mustEqual nino
        result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
        result.get(LiveInTheUkYesNoPage).get mustEqual true
        result.get(UkAddressPage).get mustEqual ukAddress
        result.get(NonUkAddressPage) mustNot be(defined)
        result.get(EmailAddressYesNoPage).get mustBe true
        result.get(EmailAddressPage).get mustBe email
        result.get(TelephoneNumberPage).get mustEqual telephoneNumber
        result.get(StartDatePage).get mustEqual startDate
      }

      "rep has passport/ID card details and non-UK address" in {

        val personalRep: IndividualPersonalRep = IndividualPersonalRep(
          name = name,
          dateOfBirth = dateOfBirth,
          identification = CombinedPassportOrIdCard("FR", "123", LocalDate.parse("2022-04-05")),
          address = nonUkAddress,
          phoneNumber = telephoneNumber,
          email = None,
          entityStart = startDate
        )

        val result = extractor(emptyUserAnswers, personalRep).get

        result.get(IndividualOrBusinessPage).get mustEqual Individual
        result.get(NamePage).get mustEqual name
        result.get(DateOfBirthPage).get mustEqual dateOfBirth
        result.get(NationalInsuranceNumberYesNoPage).get mustEqual false
        result.get(NationalInsuranceNumberPage) mustNot be(defined)
        result.get(PassportOrIdCardDetailsPage).get mustBe passportOrIdCard
        result.get(LiveInTheUkYesNoPage).get mustEqual false
        result.get(UkAddressPage) mustNot be(defined)
        result.get(NonUkAddressPage).get mustEqual nonUkAddress
        result.get(EmailAddressYesNoPage).get mustBe false
        result.get(EmailAddressPage) mustNot be(defined)
        result.get(TelephoneNumberPage).get mustEqual telephoneNumber
        result.get(StartDatePage).get mustEqual startDate
      }
    }
  }

}
