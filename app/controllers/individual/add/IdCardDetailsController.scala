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

package controllers.individual.add

import config.annotations.Individual
import controllers.actions.Actions
import forms.IdCardDetailsFormProvider

import javax.inject.Inject
import models.{IdCard, NormalMode}
import navigation.Navigator
import pages.individual.add.IdCardDetailsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.individual.add.IdCardDetailsView

import scala.concurrent.{ExecutionContext, Future}

class IdCardDetailsController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         @Individual navigator: Navigator,
                                         actions: Actions,
                                         formProvider: IdCardDetailsFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: IdCardDetailsView,
                                         val countryOptions: CountryOptions
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[IdCard] = formProvider.withPrefix("individual")

  def onPageLoad(): Action[AnyContent] = actions.authWithIndividualName {
    implicit request =>

      val preparedForm = request.userAnswers.get(IdCardDetailsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options(), request.individualName))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithIndividualName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options(), request.individualName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IdCardDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IdCardDetailsPage, NormalMode, updatedAnswers))
      )
  }
}
