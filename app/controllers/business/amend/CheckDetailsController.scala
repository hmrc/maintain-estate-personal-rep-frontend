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

package controllers.business.amend

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import handlers.ErrorHandler
import models.PersonalRepresentative
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import utils.mappers.BusinessMapper
import utils.print.BusinessPrintHelper
import viewmodels.AnswerSection
import views.html.business.amend.CheckBusinessDetailsView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckBusinessDetailsView,
                                        connector: EstatesConnector,
                                        val appConfig: FrontendAppConfig,
                                        businessPrintHelper: BusinessPrintHelper,
                                        mapper: BusinessMapper,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  def renderFromUserAnswers() : Action[AnyContent] = actions.authWithBusinessName {
    implicit request =>
      val section: AnswerSection = businessPrintHelper(request.userAnswers, provisional = false, request.businessName)
      Ok(view(Seq(section)))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithBusinessName.async {
    implicit request =>

      mapper(request.userAnswers).map {
        business =>
          connector.addOrAmendPersonalRep(request.userAnswers.utr, PersonalRepresentative(None, Some(business))).map(_ =>
            Redirect(appConfig.maintainDeclarationUrl(request.request.user.isAgent))
          )
      }.getOrElse{
        logger.error(s"[Session ID: ${Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
          s" error mapping user answers to Business Personal rep")
        errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }
  }
}
