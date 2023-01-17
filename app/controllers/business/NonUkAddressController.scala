/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.NonUkAddressFormProvider
import javax.inject.Inject
import models.{Mode, NonUkAddress}
import navigation.Navigator
import pages.business.NonUkAddressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.business.NonUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class NonUkAddressController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        @Business navigator: Navigator,
                                        actions: Actions,
                                        formProvider: NonUkAddressFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: NonUkAddressView,
                                        val countryOptions: CountryOptionsNonUK
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[NonUkAddress] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithBusinessName {
    implicit request =>

      val preparedForm = request.userAnswers.get(NonUkAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, request.businessName, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithBusinessName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, request.businessName, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NonUkAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NonUkAddressPage, mode, updatedAnswers))
      )
  }
}
