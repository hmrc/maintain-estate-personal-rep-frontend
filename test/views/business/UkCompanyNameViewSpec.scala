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

package views.business

import forms.StringFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.business.UkCompanyNameView

class UkCompanyNameViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "business.ukCompanyName"
  val name = "Name"

  override val form: Form[String] = new StringFormProvider().withPrefix(messageKeyPrefix, 53)

  "UkCompanyName view" must {

    val view = viewFor[UkCompanyNameView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    "fields" must {

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        None,
        "value"
      )
    }

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithASubmitButton(applyView(form))
  }
}
