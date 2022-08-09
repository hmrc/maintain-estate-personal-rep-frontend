/*
 * Copyright 2022 HM Revenue & Customs
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

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import utils.extractors.{BusinessExtractor, IndividualExtractor}
import javax.inject.Inject
import models.{BusinessPersonalRep, IndividualPersonalRep}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        connector: EstatesConnector,
                                        val appConfig: FrontendAppConfig,
                                        sessionRepository: SessionRepository,
                                        businessExtractor: BusinessExtractor,
                                        individualExtractor: IndividualExtractor
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def extractAndRender(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      connector.getPersonalRep(request.userAnswers.utr) flatMap {
        case individual: IndividualPersonalRep =>
          for {
            userAnswers <- Future.fromTry(individualExtractor(request.userAnswers, individual))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            Redirect(controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers())
          }
        case business: BusinessPersonalRep =>
          for {
            userAnswers <- Future.fromTry(businessExtractor(request.userAnswers, business))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            Redirect(controllers.business.amend.routes.CheckDetailsController.renderFromUserAnswers())
          }
      }
  }
}
