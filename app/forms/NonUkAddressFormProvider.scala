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

import forms.mappings.{Mappings, Validation}
import javax.inject.Inject
import models.NonUkAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}

class NonUkAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[NonUkAddress] = Form(
    mapping(
      "line1" ->
        text("nonUkAddress.line1.error.required")
          .verifying(
            firstError(
              nonEmptyString("line1", "nonUkAddress.line1.error.required"),
              maxLength(35, "nonUkAddress.line1.error.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.line1.error.invalid")
            )
          ),
      "line2" ->
        text("nonUkAddress.line2.error.required")
          .verifying(
            firstError(
              nonEmptyString("line2", "nonUkAddress.line2.error.required"),
              maxLength(35, "nonUkAddress.line2.error.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.line2.error.invalid")
            )
          ),
      "line3" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "nonUkAddress.line3.error.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.line3.error.invalid")
            )
          )
        ),
      "country" ->
        text("nonUkAddress.country.error.required")
          .verifying(
            firstError(
              maxLength(35, "nonUkAddress.country.error.length"),
              nonEmptyString("country", "nonUkAddress.country.error.required")
            )
          )
    )(NonUkAddress.apply)(NonUkAddress.unapply)
  )
}
