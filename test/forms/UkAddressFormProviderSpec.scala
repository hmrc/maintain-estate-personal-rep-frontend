/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.behaviours.StringFieldBehaviours
import forms.mappings.Validation
import models.UkAddress
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.{Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class UkAddressFormProviderSpec extends StringFieldBehaviours {

  val form: Form[UkAddress] = new UkAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "ukAddress.line1.error.required"
    val lengthKey = "ukAddress.line1.error.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
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

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

  }

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = "ukAddress.line2.error.required"
    val lengthKey = "ukAddress.line2.error.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
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

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

  }

  ".line3" must {

    val fieldName = "line3"
    val lengthKey = "ukAddress.line3.error.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )
  }

  ".line4" must {

    val fieldName = "line4"
    val lengthKey = "ukAddress.line4.error.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )
  }

  ".postcode" must {

    val fieldName = "postcode"
    val requiredKey = "ukAddress.postcode.error.required"
    val invalidKey = "ukAddress.postcode.error.invalid"

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.postcodeRegex,
      generator = arbitrary[String],
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidKey)
    )

  }

}
