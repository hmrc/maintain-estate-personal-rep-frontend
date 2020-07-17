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

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import extractors.{BusinessExtractor, IndividualExtractor}
import handlers.ErrorHandler
import javax.inject.Inject
import models.{PersonalRep, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.EstatesService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.BusinessMapper
import utils.print.BusinessPrintHelper
import viewmodels.AnswerSection

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        individualView: IndividualCheckDetailsView,
                                        businessView: BusinessCheckDetailsView,
                                        service: EstatesService,
                                        val appConfig: FrontendAppConfig,
                                        sessionRepository: SessionRepository,
                                        individualPrintHelper: IndividualPrintHelper,
                                        businessPrintHelper: BusinessPrintHelper,
                                        individualExtractor: IndividualExtractor,
                                        businessExtractor: BusinessExtractor,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def renderBusiness(userAnswers: UserAnswers,
                             name: String)
                            (implicit request: Request[AnyContent]): Result = {
    val section: AnswerSection = businessPrintHelper(userAnswers, provisional = false, name)
    Ok(businessView(section))
  }

  private def renderIndividual(userAnswers: UserAnswers,
                               name: String)
                              (implicit request: Request[AnyContent]): Result = {
    val section: AnswerSection = individualPrintHelper(userAnswers, provisional = false, name)
    Ok(individualView(section))
  }

  def extractAndRender(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      service.getPersonalRep(request.userAnswers.utr) flatMap {
        case PersonalRep(Some(individual), None) =>
          for {
            userAnswers <- Future.fromTry(individualExtractor(request.userAnswers, individual))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            renderIndividual(userAnswers, individual.name.displayName)
          }
        case PersonalRep(None, Some(business)) =>
          for {
            userAnswers <- Future.fromTry(businessExtractor(request.userAnswers, business))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            renderBusiness(userAnswers, business.name)
          }
      }
  }
}
