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

import forms.mappings.{Mappings, Validation}
import javax.inject.Inject
import models.UkAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}

class UkAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("ukAddress.line1.error.required")
          .verifying(
            firstError(
              nonEmptyString("line1", "ukAddress.line1.error.required"),
              maxLength(35, "ukAddress.line1.error.length"),
              regexp(Validation.addressLineRegex, "ukAddress.line1.error.invalid")
            )
          ),
      "line2" ->
        text("ukAddress.line2.error.required")
          .verifying(
            firstError(
              nonEmptyString("line2", "ukAddress.line2.error.required"),
              maxLength(35, "ukAddress.line2.error.length"),
              regexp(Validation.addressLineRegex, "ukAddress.line2.error.invalid")
            )
          ),
      "line3" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "ukAddress.line3.error.length"),
              regexp(Validation.addressLineRegex, "ukAddress.line3.error.invalid")
            )
          )
        ),
      "line4" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "ukAddress.line4.error.length"),
              regexp(Validation.addressLineRegex, "ukAddress.line4.error.invalid")
            )
          )
        ),
      "postcode" ->
        postcode("ukAddress.postcode.error.required", "ukAddress.postcode.error.invalid")
          .verifying(
            firstError(
              nonEmptyString("postcode", "ukAddress.postcode.error.required"),
              regexp(Validation.postcodeRegex, "ukAddress.postcode.error.invalid")
            )
          )
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
