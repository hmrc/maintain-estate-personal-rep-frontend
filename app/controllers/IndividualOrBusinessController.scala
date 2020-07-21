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

package controllers

import controllers.actions.Actions
import forms.IndividualOrBusinessFormProvider
import javax.inject.Inject
import models.IndividualOrBusiness._
import models.{Enumerable, IndividualOrBusiness, Mode, NormalMode}
import pages.IndividualOrBusinessPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndividualOrBusinessView

import scala.concurrent.{ExecutionContext, Future}

class IndividualOrBusinessController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                registrationsRepository: SessionRepository,
                                                actions: Actions,
                                                formProvider: IndividualOrBusinessFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: IndividualOrBusinessView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[IndividualOrBusiness] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualOrBusinessPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualOrBusinessPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield {
            value match {
              case Individual =>
                Redirect(controllers.individual.routes.NameController.onPageLoad(mode))
              case Business =>
                Redirect(controllers.business.routes.UkRegisteredCompanyYesNoController.onPageLoad(mode))
            }
          }
        }
      )
  }
}
