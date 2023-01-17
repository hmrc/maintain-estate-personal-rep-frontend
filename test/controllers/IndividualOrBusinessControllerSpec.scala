/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import base.SpecBase
import controllers.routes._
import forms.IndividualOrBusinessFormProvider
import models.{CheckMode, IndividualOrBusiness, NormalMode}
import pages.IndividualOrBusinessPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.IndividualOrBusinessView

class IndividualOrBusinessControllerSpec extends SpecBase {

  lazy val individualOrBusinessRoute: String = routes.IndividualOrBusinessController.onPageLoad(NormalMode).url
  lazy val individualOrBusinessChangeRoute: String = routes.IndividualOrBusinessController.onPageLoad(CheckMode).url

  val formProvider = new IndividualOrBusinessFormProvider()
  val form: Form[IndividualOrBusiness] = formProvider()

  "IndividualOrBusiness Controller" when {

    "Check mode" must {

      "navigate to normal mode when selecting a different type" in {

        val previousAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, IndividualOrBusiness.Business)
          .success.value

        val application =
          applicationBuilder(userAnswers = Some(previousAnswers)).build()

        val request =
          FakeRequest(POST, individualOrBusinessChangeRoute)
            .withFormUrlEncodedBody(("value", IndividualOrBusiness.Individual.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.individual.routes.NameController.onPageLoad(NormalMode).url

        application.stop()
      }

      "navigate to check mode when updating the same type" in {

        val previousAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, IndividualOrBusiness.Business)
          .success.value

        val application =
          applicationBuilder(userAnswers = Some(previousAnswers)).build()

        val request =
          FakeRequest(POST, individualOrBusinessChangeRoute)
            .withFormUrlEncodedBody(("value", IndividualOrBusiness.Business.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.business.routes.UkRegisteredCompanyYesNoController.onPageLoad(CheckMode).url

        application.stop()
      }

    }

    "Normal mode" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, individualOrBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndividualOrBusinessView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode)(request, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = emptyUserAnswers.set(IndividualOrBusinessPage, IndividualOrBusiness.values.head).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, individualOrBusinessRoute)

        val view = application.injector.instanceOf[IndividualOrBusinessView]

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(IndividualOrBusiness.values.head), NormalMode)(request, messages).toString

        application.stop()
      }

      "redirect to individual name controller when INDIVIDUAL is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, individualOrBusinessRoute)
            .withFormUrlEncodedBody(("value", IndividualOrBusiness.Individual.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.individual.routes.NameController.onPageLoad(NormalMode).url

        application.stop()
      }

      "redirect to uk registered company yes no controller when BUSINESS is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, individualOrBusinessRoute)
            .withFormUrlEncodedBody(("value", IndividualOrBusiness.Business.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.business.routes.UkRegisteredCompanyYesNoController.onPageLoad(NormalMode).url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, individualOrBusinessRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[IndividualOrBusinessView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode)(request, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, individualOrBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, individualOrBusinessRoute)
            .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

        application.stop()
      }
    }


  }
}
