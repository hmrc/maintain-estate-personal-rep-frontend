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

package controllers

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockitoSugar {

  "Index Controller" must {

    "populate user answers with UTR and DOD and redirect to Individual or Business" in {

      val utr: String = "UTR"
      val dateOfDeath: String = "1996-02-03"

      val mockEstatesConnector: EstatesConnector = mock[EstatesConnector]
      val mockSessionRepository : SessionRepository = mock[SessionRepository]

      when(mockEstatesConnector.getDateOfDeath(any())(any(), any())).thenReturn(Future.successful(Json.toJson(dateOfDeath)))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(utr).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.IndividualOrBusinessController.onPageLoad(NormalMode).url)

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(uaCaptor.capture)
      uaCaptor.getValue.utr mustEqual utr
      uaCaptor.getValue.dateOfDeath mustEqual LocalDate.parse(dateOfDeath)

      application.stop()
    }
  }
}
