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

package controllers.actions

import com.google.inject.Inject
import models.requests.{BusinessNameRequest, DataRequest, IndividualNameRequest, OptionalDataRequest}
import play.api.mvc.{ActionBuilder, AnyContent}

class Actions @Inject()(
                         identify: IdentifierAction,
                         getData: DataRetrievalAction,
                         requireData: DataRequiredAction,
                         verifyUtr: UTRAuthenticationAction,
                         requireBusinessName: business.NameRequiredAction,
                         requireIndividualName: individual.NameRequiredAction
                       ) {

  def authWithSession: ActionBuilder[OptionalDataRequest, AnyContent] =
    identify andThen getData

  def authWithData: ActionBuilder[DataRequest, AnyContent] =
    authWithSession andThen requireData

  def authenticatedForUtr: ActionBuilder[DataRequest, AnyContent] =
    authWithData andThen verifyUtr

  def authWithBusinessName: ActionBuilder[BusinessNameRequest, AnyContent] =
    authWithData andThen requireBusinessName

  def authWithIndividualName: ActionBuilder[IndividualNameRequest, AnyContent] =
    authWithData andThen requireIndividualName

}
