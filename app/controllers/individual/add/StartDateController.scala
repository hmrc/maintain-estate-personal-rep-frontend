/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.individual.add

import java.time.LocalDate

import config.annotations.Individual
import controllers.actions.Actions
import forms.DateFormProvider
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.individual.add.StartDatePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.individual.add.StartDateView

import scala.concurrent.{ExecutionContext, Future}

class StartDateController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     val controllerComponents: MessagesControllerComponents,
                                     sessionRepository: SessionRepository,
                                     @Individual navigator: Navigator,
                                     actions: Actions,
                                     formProvider: DateFormProvider,
                                     view: StartDateView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithIndividualName {
    implicit request =>

      val form: Form[LocalDate] = formProvider.withConfig("individual.startDate", request.userAnswers.dateOfDeath)

      val preparedForm = request.userAnswers.get(StartDatePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.individualName))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithIndividualName.async {
    implicit request =>

      val form: Form[LocalDate] = formProvider.withConfig("individual.startDate", request.userAnswers.dateOfDeath)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.individualName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(StartDatePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(StartDatePage, NormalMode, updatedAnswers))
      )
  }
}
