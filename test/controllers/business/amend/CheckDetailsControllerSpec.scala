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

package controllers.business.amend

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.IndividualOrBusiness.Business
import models.UkAddress
import models.requests.AgentUser
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.IndividualOrBusinessPage
import pages.business._
import pages.business.add.StartDatePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.http.HttpResponse
import utils.print.BusinessPrintHelper
import views.html.business.amend.CheckBusinessDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private lazy val checkDetailsRoute = controllers.business.amend.routes.CheckDetailsController.renderFromUserAnswers().url
  private lazy val submitDetailsRoute = controllers.business.amend.routes.CheckDetailsController.onSubmit().url

  private val name = "Test"
  private val utr = "1234567890"
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val telephoneNumber: String = "999"
  private val startDate = LocalDate.parse("2010-02-03")

  private val userAnswers = emptyUserAnswers
    .set(IndividualOrBusinessPage, Business).success.value
    .set(UkRegisteredCompanyYesNoPage, true).success.value
    .set(NamePage, name).success.value
    .set(UtrPage, utr).success.value
    .set(AddressUkYesNoPage, true).success.value
    .set(UkAddressPage, ukAddress).success.value
    .set(EmailAddressYesNoPage, false).success.value
    .set(TelephoneNumberPage, telephoneNumber).success.value
    .set(StartDatePage, startDate).success.value

  "Business Amend - CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckBusinessDetailsView]
      val printHelper = application.injector.instanceOf[BusinessPrintHelper]
      val answerSection = printHelper(userAnswers, provisional = false, name)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(answerSection))(request, messages).toString
    }

    "submitting" when {

      val mockEstatesConnector = mock[EstatesConnector]
      when(mockEstatesConnector.addOrAmendPersonalRep(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

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
              user = AgentUser("id", Enrolments(Set()), "arn"),
              affinityGroup = AffinityGroup.Agent)
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
