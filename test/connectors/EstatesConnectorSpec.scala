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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.{BusinessPersonalRep, IndividualPersonalRep, Name, NationalInsuranceNumber, PersonalRepresentative, UkAddress}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration

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
  val email: String = "email@example.com"

  "estates connector" when {

    "add or amend personal rep" when {

      def addOrAmendPersonalRepUrl(utr: String) =
        s"/estates/personal-rep/add-or-amend/$utr"

      val business = BusinessPersonalRep(
        name = "Name",
        phoneNumber = "999",
        utr = None,
        address = UkAddress("Line 1", "Line 2", None, None, "POSTCODE"),
        email = Some(email),
        entityStart = LocalDate.parse("2020-03-27")
      )

      val individual = IndividualPersonalRep(
        name = Name("First", None, "Last"),
        dateOfBirth = LocalDate.parse("2019-02-03"),
        identification = NationalInsuranceNumber("nino"),
        address = UkAddress("Line 1", "Line 2", None, None, "POSTCODE"),
        phoneNumber = "tel",
        email = Some(email),
        entityStart = LocalDate.parse("2020-03-27")
      )

      "business" must {

        val personalRep = PersonalRepresentative(None, Some(business))

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
            post(urlEqualTo(addOrAmendPersonalRepUrl(utr)))
              .willReturn(ok)
          )

          val result = connector.addOrAmendPersonalRep(utr, personalRep)

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
            post(urlEqualTo(addOrAmendPersonalRepUrl(utr)))
              .willReturn(badRequest)
          )

          val result = connector.addOrAmendPersonalRep(utr, personalRep)

          result.map(response => response.status mustBe BAD_REQUEST)

          application.stop()
        }
      }

      "individual" must {

        val personalRep = PersonalRepresentative(Some(individual), None)

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
            post(urlEqualTo(addOrAmendPersonalRepUrl(utr)))
              .willReturn(ok)
          )

          val result = connector.addOrAmendPersonalRep(utr, personalRep)

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
            post(urlEqualTo(addOrAmendPersonalRepUrl(utr)))
              .willReturn(badRequest)
          )

          val result = connector.addOrAmendPersonalRep(utr, personalRep)

          result.map(response => response.status mustBe BAD_REQUEST)

          application.stop()
        }
      }

    }

    "get date of death" in {

      val utr = "1000000008"

      val expectedJson = Json.toJson("1996-02-03")

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        get(urlEqualTo(s"/estates/$utr/date-of-death"))
          .willReturn(okJson(expectedJson.toString))
      )

      val result  = Await.result(connector.getDateOfDeath(utr), Duration.Inf)
      result mustBe expectedJson
    }

    "get personal rep" when {

      val orgName = "Org"
      val name = Name("John", None, "Doe")
      val utr = "1234567890"
      val nino = "AA000000A"
      val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "POSTCODE")
      val telephoneNumber: String = "999"
      val date = LocalDate.parse("2010-02-03")

      "individual" in {

        val personalRep: IndividualPersonalRep = IndividualPersonalRep(
          name = name,
          dateOfBirth = date,
          identification = NationalInsuranceNumber(nino),
          address = ukAddress,
          phoneNumber = telephoneNumber,
          email = Some(email),
          entityStart = date
        )

        val expectedJson = Json.toJson(personalRep)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(s"/estates/$utr/transformed/personal-representative"))
            .willReturn(okJson(expectedJson.toString))
        )

        val result  = Await.result(connector.getPersonalRep(utr), Duration.Inf)
        result mustBe personalRep
      }

      "business" in {

        val personalRep: BusinessPersonalRep = BusinessPersonalRep(
          name = orgName,
          phoneNumber = telephoneNumber,
          utr = Some(utr),
          address = ukAddress,
          email = Some(email),
          entityStart = date
        )

        val expectedJson = Json.toJson(personalRep)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(s"/estates/$utr/transformed/personal-representative"))
            .willReturn(okJson(expectedJson.toString))
        )

        val result  = Await.result(connector.getPersonalRep(utr), Duration.Inf)
        result mustBe personalRep
      }

    }

  }
}
