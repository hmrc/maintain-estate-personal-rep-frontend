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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen
import forms.mappings.Validation

class NationalInsuranceNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "individual.nationalInsuranceNumber.error.required"
  val invalidFormatKey = "individual.nationalInsuranceNumber.error.invalidFormat"

  val form = new NationalInsuranceNumberFormProvider().withPrefix("individual.nationalInsuranceNumber")

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.ninoRegex)
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

    behave like ninoField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidFormatKey, Seq(fieldName))
    )
  }
}
