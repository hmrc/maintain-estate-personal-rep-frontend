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

package utils.extractors

import java.time.LocalDate

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{BusinessPersonalRep, NonUkAddress, UkAddress}
import pages.IndividualOrBusinessPage
import pages.business._
import pages.business.add.StartDatePage

class BusinessExtractorSpec extends SpecBase {

  private val name: String = "Name"
  private val utr: String = "1234567890"
  private val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  private val telephoneNumber: String = "999"
  private val startDate: LocalDate = LocalDate.parse("2020-01-01")
  private val email: String = "email@example.com"

  "Business extractor" must {

    val extractor = injector.instanceOf[BusinessExtractor]

    "populate user answers" when {

      "rep is UK registered, has UK address and an email" in {

        val personalRep: BusinessPersonalRep = BusinessPersonalRep(
          name = name,
          phoneNumber = telephoneNumber,
          utr = Some(utr),
          address = ukAddress,
          email = Some(email),
          entityStart = startDate
        )

        val result = extractor(emptyUserAnswers, personalRep).get

        result.get(IndividualOrBusinessPage).get mustEqual Business
        result.get(UkRegisteredCompanyYesNoPage).get mustEqual true
        result.get(NamePage).get mustEqual name
        result.get(UtrPage).get mustEqual utr
        result.get(AddressUkYesNoPage).get mustEqual true
        result.get(UkAddressPage).get mustEqual ukAddress
        result.get(NonUkAddressPage) mustNot be(defined)
        result.get(EmailAddressYesNoPage).get mustBe true
        result.get(EmailAddressPage).get mustBe email
        result.get(TelephoneNumberPage).get mustEqual telephoneNumber
        result.get(StartDatePage).get mustEqual startDate
      }

      "rep is not UK registered and has non-UK address" in {

        val personalRep: BusinessPersonalRep = BusinessPersonalRep(
          name = name,
          phoneNumber = telephoneNumber,
          utr = None,
          address = nonUkAddress,
          email = None,
          entityStart = startDate
        )

        val result = extractor(emptyUserAnswers, personalRep).get

        result.get(IndividualOrBusinessPage).get mustEqual Business
        result.get(UkRegisteredCompanyYesNoPage).get mustEqual false
        result.get(NamePage).get mustEqual name
        result.get(UtrPage) mustNot be(defined)
        result.get(AddressUkYesNoPage).get mustEqual false
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
