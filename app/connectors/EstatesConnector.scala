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

package connectors

import config.FrontendAppConfig
import models.{PersonalRep, PersonalRepresentative}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) {

  private def getPersonalRepUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/transformed/personal-representative"

  def getPersonalRep(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PersonalRep] = {
    val fullUrl = getPersonalRepUrl(utr)
    http.get(url"$fullUrl").execute[PersonalRep]
  }

  private def addOrAmendPersonalRepUrl(utr: String) = s"${config.estatesUrl}/estates/personal-rep/add-or-amend/$utr"

  def addOrAmendPersonalRep(utr: String, personalRep: PersonalRepresentative)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = addOrAmendPersonalRepUrl(utr)
    http.post(url"$fullUrl")
      .withBody(Json.toJson(personalRep))
      .execute[HttpResponse]
  }

  private def getDateOfDeathUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/date-of-death"

  def getDateOfDeath(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[JsValue] = {
    val fullUrl = getDateOfDeathUrl(utr)
    http.get(url"$fullUrl").execute[JsValue]
  }

}
