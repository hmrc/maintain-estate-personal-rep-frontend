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

package controllers.individual.amend

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import handlers.ErrorHandler
import javax.inject.Inject
import models.PersonalRepresentative
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.IndividualMapper
import utils.print.IndividualPrintHelper
import viewmodels.AnswerSection
import views.html.individual.amend.CheckIndividualDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckIndividualDetailsView,
                                        connector: EstatesConnector,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: IndividualPrintHelper,
                                        mapper: IndividualMapper,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def renderFromUserAnswers() : Action[AnyContent] = actions.authWithIndividualName {
    implicit request =>
      val section: AnswerSection = printHelper(request.userAnswers, provisional = false, request.individualName)
      Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      mapper(request.userAnswers).map {
        individual =>
          connector.addOrAmendPersonalRep(request.userAnswers.utr, PersonalRepresentative(Some(individual), None)).map(_ =>
            // TODO - pattern match on AffinityGroup and redirect to relevant declaration
            Redirect(controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers())
          )
      }.getOrElse(Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate)))
  }
}
