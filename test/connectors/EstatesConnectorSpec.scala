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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.{BusinessPersonalRep, UkAddress}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits._

class EstatesConnectorSpec extends SpecBase with Generators with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  val utr = "1000000008"
  val index = 0
  val description = "description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "estates connector" when {

    "add business personal rep" must {

      def addBusinessPersonalRepUrl(utr: String) =
        s"/estates/personal-rep/add-business/$utr"

      val business = BusinessPersonalRep(
        name = "Name",
        phoneNumber = "999",
        utr = None,
        address = UkAddress("Line 1", "Line 2", None, None, "POSTCODE"),
        entityStart = LocalDate.parse("2020-03-27"),
        provisional = true
      )

      "return OK when the request is successful" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          post(urlEqualTo(addBusinessPersonalRepUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addBusinessPersonalRep(utr, business)

        result.futureValue.status mustBe OK

        application.stop()
      }

      "return Bad Request when the request is unsuccessful" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          post(urlEqualTo(addBusinessPersonalRepUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addBusinessPersonalRep(utr, business)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

  }
}
