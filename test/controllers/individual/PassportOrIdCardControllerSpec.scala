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

package controllers.individual

import base.SpecBase
import controllers.routes._
import forms.PassportOrIdCardFormProvider
import models.{Name, NormalMode, PassportOrIdCard}
import pages.individual.{NamePage, PassportOrIdCardPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.individual.PassportOrIdCardView

class PassportOrIdCardControllerSpec extends SpecBase {

  lazy val PassportOrIdCardRoute: String = routes.PassportOrIdCardController.onPageLoad(NormalMode).url

  val formProvider = new PassportOrIdCardFormProvider()
  val form: Form[PassportOrIdCard] = formProvider()

  val name = Name("FirstName", None, "LastName")
  val userAnswersWithName = emptyUserAnswers.set(NamePage, name)
    .success.value

  "PassportOrIdCard Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val request = FakeRequest(GET, PassportOrIdCardRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PassportOrIdCardView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, name.displayName, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithName
        .set(PassportOrIdCardPage, PassportOrIdCard.values.head).success.value))
        .build()

      val request = FakeRequest(GET, PassportOrIdCardRoute)

      val view = application.injector.instanceOf[PassportOrIdCardView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PassportOrIdCard.values.head), name.displayName, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Passport controller when Passport is submitted" ignore {

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val request =
        FakeRequest(POST, PassportOrIdCardRoute)
          .withFormUrlEncodedBody(("value", PassportOrIdCard.Passport.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to IdCard controller when IdCard is submitted" ignore {

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val request =
        FakeRequest(POST, PassportOrIdCardRoute)
          .withFormUrlEncodedBody(("value", PassportOrIdCard.IdCard.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val request =
        FakeRequest(POST, PassportOrIdCardRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PassportOrIdCardView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name.displayName, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, PassportOrIdCardRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, PassportOrIdCardRoute)
          .withFormUrlEncodedBody(("value", PassportOrIdCard.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
