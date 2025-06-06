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

import forms.helpers.FormHelper._
import forms.mappings.{Mappings, Validation}
import models.Name
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class NameFormProvider @Inject() extends Mappings {

  def withPrefix(prefix: String): Form[Name] = Form(
    mapping(
      "firstName" -> text(s"$prefix.error.firstName.required")
        .verifying(
          firstError(
            maxLength(35, s"$prefix.error.firstName.length"),
            nonEmptyString("firstName", s"$prefix.error.firstName.required"),
            regexp(Validation.nameRegex, s"$prefix.error.firstName.invalid")
          )
        ),
      "middleName" -> optional(text()
        .verifying(
          firstError(
            maxLength(35, s"$prefix.error.middleName.length"),
            regexp(Validation.nameRegex, s"$prefix.error.middleName.invalid"))
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "lastName" -> text(s"$prefix.error.lastName.required")
        .verifying(
          firstError(
            maxLength(35, s"$prefix.error.lastName.length"),
            nonEmptyString("lastName", s"$prefix.error.lastName.required"),
            regexp(Validation.nameRegex, s"$prefix.error.lastName.invalid")
          )
        )
    )(Name.apply)(Name.unapply)
  )
}
