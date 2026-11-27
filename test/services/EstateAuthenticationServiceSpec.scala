/*
 * Copyright 2026 HM Revenue & Customs
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

package services

import base.SpecBase
import connectors.EstatesAuthConnector
import models.UserAnswers
import models.auth.{AuthAgentAllowed, AuthAllowed, AuthDenied, AuthInternalServerError}
import models.requests.{DataRequest, OrganisationUser}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Results.{Redirect, Unauthorized}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.*
import scala.concurrent.{ExecutionContext, Future}

class EstateAuthenticationServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

  given HeaderCarrier = HeaderCarrier()

  private val utr         = "1234567890"
  private val arn         = "SARN1234567"
  private val redirectUrl = "/some-redirect-url"

  private val user        = OrganisationUser("id", Enrolments(Set()))
  private val userAnswers = new UserAnswers("id", utr, frontendAppConfig.minDate)

  "EstateAuthenticationService" when {

    "authenticateAgent" when {

      "agent is authorised" must {

        "return Right with ARN" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.agentIsAuthorised(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthAgentAllowed(arn)))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateAgent()

          whenReady(result) { r =>
            r mustBe Right(arn)
          }
        }
      }

      "agent is denied" must {

        "return Left with Redirect" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.agentIsAuthorised(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthDenied(redirectUrl)))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateAgent()

          whenReady(result) { r =>
            r mustBe Left(Redirect(redirectUrl))
          }
        }
      }

      "an unexpected response is returned" must {

        "return Left with Unauthorized" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.agentIsAuthorised(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthInternalServerError))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateAgent()

          whenReady(result) { r =>
            r mustBe Left(Unauthorized)
          }
        }
      }
    }

    "authenticateForUtr" when {

      given DataRequest[?] = DataRequest(fakeRequest, userAnswers, user)

      "user is authorised for UTR" must {

        "return Right with the DataRequest" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.authorisedForUtr(any[String])(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthAllowed()))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateForUtr(utr)

          whenReady(result) { r =>
            r mustBe Right(summon[DataRequest[?]])
          }
        }
      }

      "user is denied for UTR" must {

        "return Left with Redirect" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.authorisedForUtr(any[String])(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthDenied(redirectUrl)))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateForUtr(utr)

          whenReady(result) { r =>
            r mustBe Left(Redirect(redirectUrl))
          }
        }
      }

      "an unexpected response is returned" must {

        "return Left with Unauthorized" in {

          val mockConnector = mock[EstatesAuthConnector]
          when(mockConnector.authorisedForUtr(any[String])(using any[HeaderCarrier], any[ExecutionContext]))
            .thenReturn(Future.successful(AuthInternalServerError))

          val service = new EstateAuthenticationServiceImpl(mockConnector)

          val result = service.authenticateForUtr(utr)

          whenReady(result) { r =>
            r mustBe Left(Unauthorized)
          }
        }
      }
    }
  }

}
