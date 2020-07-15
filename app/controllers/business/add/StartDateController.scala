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

import java.time.LocalDate

import config.annotations.Business
import controllers.actions.Actions
import forms.DateFormProvider
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.business.StartDatePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.business.add.StartDateView

import scala.concurrent.{ExecutionContext, Future}

class StartDateController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     val controllerComponents: MessagesControllerComponents,
                                     sessionRepository: SessionRepository,
                                     @Business navigator: Navigator,
                                     actions: Actions,
                                     formProvider: DateFormProvider,
                                     view: StartDateView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[LocalDate] = formProvider.withPrefix("business.startDate")

  def onPageLoad(): Action[AnyContent] = actions.authWithBusinessName {
    implicit request =>

      val preparedForm = request.userAnswers.get(StartDatePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.businessName))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithBusinessName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.businessName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(StartDatePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(StartDatePage, NormalMode, updatedAnswers))
      )
  }
}
