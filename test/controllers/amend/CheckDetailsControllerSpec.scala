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

package controllers.amend

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.{BusinessPersonalRep, IndividualPersonalRep, Name, NationalInsuranceNumber, UkAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private lazy val checkDetailsRoute = controllers.amend.routes.CheckDetailsController.extractAndRender().url

  private val orgName = "Org"
  private val name = Name("John", None, "Doe")
  private val utr = "1234567890"
  private val nino = "AA000000A"
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val telephoneNumber: String = "999"
  private val date = LocalDate.parse("2010-02-03")

  "CheckDetails Controller" when {

    "individual personal rep" must {

      val personalRep: IndividualPersonalRep = IndividualPersonalRep(
        name = name,
        dateOfBirth = date,
        identification = NationalInsuranceNumber(nino),
        address = ukAddress,
        phoneNumber = telephoneNumber,
        email = None,
        entityStart = date
      )

      "extract answers and redirect" in {

        val mockEstatesConnector = mock[EstatesConnector]
        when(mockEstatesConnector.getPersonalRep(any())(any(), any())).thenReturn(Future.successful(personalRep))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers().url
      }
    }

    "business personal rep" must {

      val personalRep: BusinessPersonalRep = BusinessPersonalRep(
        name = orgName,
        phoneNumber = telephoneNumber,
        utr = Some(utr),
        address = ukAddress,
        entityStart = date
      )

      "extract answers and redirect" in {

        val mockEstatesConnector = mock[EstatesConnector]
        when(mockEstatesConnector.getPersonalRep(any())(any(), any())).thenReturn(Future.successful(personalRep))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.business.amend.routes.CheckDetailsController.renderFromUserAnswers().url
      }
    }

  }
}
