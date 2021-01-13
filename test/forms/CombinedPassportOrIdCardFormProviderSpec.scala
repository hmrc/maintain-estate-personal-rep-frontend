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

package forms

import java.time.LocalDate

import base.FakeEstatesApp
import forms.behaviours.PassportOrIdCardFieldBehaviours
import models.CombinedPassportOrIdCard
import play.api.data.{Form, FormError}

class CombinedPassportOrIdCardFormProviderSpec extends PassportOrIdCardFieldBehaviours with FakeEstatesApp {

  val prefix = "individual.passportOrIdCardDetails"

  val requiredKey = s"$prefix.country.error.required"
  val lengthKey = s"$prefix.country.error.length"
  val maxLengthCountryField = 100
  val maxLengthNumberField = 30

  val form: Form[CombinedPassportOrIdCard] =
    new CombinedPassportOrIdCardDetailsFormProvider(frontendAppConfig).withPrefix(prefix)

  ".country" must {

    val fieldName = "country"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthCountryField)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthCountryField,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthCountryField))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".number" must {

    val requiredKey = s"$prefix.number.error.required"
    val lengthKey = s"$prefix.number.error.length"
    val maxLength = 30

    val fieldName = "number"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".expiryDate" should {

    val min: LocalDate = frontendAppConfig.minDate
    val max: LocalDate = frontendAppConfig.maxDate

    val validData = datesBetween(
      min = min,
      max = max
    )

    val fieldName = "expiryDate"

    behave like dateField(
      form,
      fieldName,
      validData
    )

    behave like mandatoryDateField(
      form,
      fieldName,
      s"$prefix.expiryDate.error.required.all"
    )

    behave like dateFieldWithMax(
      form,
      fieldName,
      max = max,
      FormError(fieldName, s"$prefix.expiryDate.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(
      form,
      fieldName,
      min = min,
      FormError(fieldName, s"$prefix.expiryDate.error.past", List("day", "month", "year"))
    )

  }

}
