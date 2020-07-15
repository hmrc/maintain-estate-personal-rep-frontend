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

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{NonUkAddress, UkAddress}
import pages.IndividualOrBusinessPage
import pages.business._

class BusinessMapperSpec extends SpecBase {

  private val name = "Name"
  private val utr = "1234567890"
  private val startDate = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val telephoneNumber: String = "999"
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")

  "Business mapper" when {

    val mapper = injector.instanceOf[BusinessMapper]

    val baseAnswers = emptyUserAnswers
      .set(IndividualOrBusinessPage, Business).success.value
      .set(NamePage, name).success.value
      .set(TelephoneNumberPage, telephoneNumber).success.value
      .set(StartDatePage, startDate).success.value

    "generate UK registered business personal rep model with UK address" in {

      val userAnswers = baseAnswers
        .set(UkRegisteredCompanyYesNoPage, true).success.value
        .set(UtrPage, utr).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.utr mustBe Some(utr)
      result.address mustBe ukAddress
      result.entityStart mustBe startDate
    }

    "generate non-UK business personal rep model with non-UK address" in {

      val userAnswers = baseAnswers
        .set(UkRegisteredCompanyYesNoPage, false).success.value
        .set(AddressUkYesNoPage, false).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.utr mustBe None
      result.address mustBe nonUkAddress
      result.entityStart mustBe startDate
    }
  }
}
