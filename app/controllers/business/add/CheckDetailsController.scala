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

package controllers.business.add

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.BusinessMapper
import utils.print.BusinessPrintHelper
import viewmodels.AnswerSection
import views.html.business.add.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: BusinessPrintHelper,
                                        mapper: BusinessMapper,
                                        connector: EstatesConnector
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithBusinessName {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers, provisional = true, request.businessName)
      Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithBusinessName.async {
    implicit request =>

      mapper(request.userAnswers) match {
        case None =>
          Future.successful(InternalServerError)
        case Some(business) =>
          connector.addBusinessPersonalRep(request.userAnswers.utr, business).map(_ =>
            // TODO - add User to request, pattern match on AffinityGroup and redirect to relevant declaration
            Redirect(controllers.business.add.routes.CheckDetailsController.onPageLoad())
          )
      }
  }
}
