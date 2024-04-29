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

package controllers.business

import config.annotations.Business
import controllers.actions.Actions
import forms.EmailAddressFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.business.EmailAddressPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.business.EmailAddressView

import scala.concurrent.{ExecutionContext, Future}

class EmailAddressController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        actions: Actions,
                                        formProvider: EmailAddressFormProvider,
                                        view: EmailAddressView,
                                        repository: SessionRepository,
                                        @Business navigator: Navigator
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[String] = formProvider.withPrefix("business.email")

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithBusinessName {
    implicit request =>

      val preparedForm = request.userAnswers.get(EmailAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.businessName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithBusinessName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.businessName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EmailAddressPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EmailAddressPage, mode, updatedAnswers))
      )
  }
}
