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

package controllers.individual

import config.annotations.Individual
import controllers.actions.Actions
import forms.EmailAddressFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.individual.EmailAddressPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.individual.EmailAddressView

import scala.concurrent.{ExecutionContext, Future}

class EmailAddressController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        actions: Actions,
                                        formProvider: EmailAddressFormProvider,
                                        view: EmailAddressView,
                                        repository: SessionRepository,
                                        @Individual navigator: Navigator
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[String] = formProvider.withPrefix("individual.email")

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithIndividualName {
    implicit request =>

      val preparedForm = request.userAnswers.get(EmailAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.individualName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithIndividualName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.individualName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EmailAddressPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EmailAddressPage, mode, updatedAnswers))
      )
  }
}
