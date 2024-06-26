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

package views.individual.add

import java.time.LocalDate

import forms.DateFormProvider
import models.Name
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.individual.add.StartDateView

class StartDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  val prefix = "individual.startDate"
  val name = Name("FirstName", None, "LastName")

  override val form: Form[LocalDate] = new DateFormProvider(frontendAppConfig).withConfig(prefix)

  val view: StartDateView = viewFor[StartDateView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, name.displayName)(fakeRequest, messages)

  "StartDate View" must {

    behave like dynamicTitlePage(applyView(form), prefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(
      form,
      applyView,
      prefix,
      "value",
      name.displayName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
