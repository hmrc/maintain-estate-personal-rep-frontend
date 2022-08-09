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

package controllers

import java.time.LocalDate

import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsString, JsValue}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: Actions,
                                 repository: SessionRepository,
                                 connector: EstatesConnector,
                                 config: FrontendAppConfig
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(utr: String, mode: Mode): Action[AnyContent] = actions.authWithSession.async {
    implicit request =>

      def userAnswers(dateOfDeath: JsValue): UserAnswers = UserAnswers(
        id = request.user.internalId,
        utr = utr,
        dateOfDeath = dateOfDeath match {
          case JsString(date) => LocalDate.parse(date)
          case _ => config.minDate
        }
      )

      for {
        dateOfDeath <- connector.getDateOfDeath(utr)
        ua <- Future.successful(
          if (mode == NormalMode) {
            userAnswers(dateOfDeath)
          } else {
            request.userAnswers.getOrElse(userAnswers(dateOfDeath))
          }
        )
        _ <- repository.set(ua)
      } yield {
        if (mode == NormalMode) {
          Redirect(controllers.routes.IndividualOrBusinessController.onPageLoad(NormalMode))
        } else {
          Redirect(controllers.amend.routes.CheckDetailsController.extractAndRender())
        }
      }
  }
}
