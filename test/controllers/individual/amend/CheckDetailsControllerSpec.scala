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

package controllers.individual.amend

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.IndividualOrBusiness.Individual
import models.requests.AgentUser
import models.{Name, UkAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.IndividualOrBusinessPage
import pages.individual._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HttpResponse
import utils.print.IndividualPrintHelper
import views.html.individual.amend.CheckIndividualDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private lazy val checkDetailsRoute = controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers().url
  private lazy val submitDetailsRoute = controllers.individual.amend.routes.CheckDetailsController.onSubmit().url

  private val name: Name = Name("John", None, "Doe")
  private val dateOfBirth = LocalDate.parse("1996-03-09")
  private val nino = "AA000000A"
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val telephoneNumber: String = "999"
  private val startDate = LocalDate.parse("2019-03-09")

  private val userAnswers = emptyUserAnswers
    .set(IndividualOrBusinessPage, Individual).success.value
    .set(NamePage, name).success.value
    .set(DateOfBirthPage, dateOfBirth).success.value
    .set(NationalInsuranceNumberYesNoPage, true).success.value
    .set(NationalInsuranceNumberPage, nino).success.value
    .set(LiveInTheUkYesNoPage, true).success.value
    .set(UkAddressPage, ukAddress).success.value
    .set(TelephoneNumberPage, telephoneNumber).success.value
    .set(StartDatePage, startDate).success.value

  "Individual Amend - CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckIndividualDetailsView]
      val printHelper = application.injector.instanceOf[IndividualPrintHelper]
      val answerSection = printHelper(userAnswers, provisional = false, name.displayFullName)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection)(fakeRequest, messages).toString
    }

    "submitting" when {

      val mockEstatesConnector = mock[EstatesConnector]
      when(mockEstatesConnector.addOrAmendPersonalRep(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

      "individual" must {
        "redirect to declaration page" in {

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
              .build()

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual "http://localhost:8828/maintain-an-estate/declaration"

          application.stop()
        }
      }

      "agent" must {
        "redirect to agent declaration questions" in {

          val application =
            applicationBuilderForUser(userAnswers = Some(userAnswers),
              user = AgentUser("id", Enrolments(Set()), "arn"))
              .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
              .build()

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual "http://localhost:8828/maintain-an-estate/is-agency-address-in-uk"

          application.stop()
        }
      }
    }

  }
}
