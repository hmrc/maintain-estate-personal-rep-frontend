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

package utils.print

import com.google.inject.Inject
import models.{Address, CombinedPassportOrIdCard, IdCard, Name, Passport, UserAnswers}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.AnswerRow

import java.time.LocalDate

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, name: String)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name)

  class Bound(userAnswers: UserAnswers, name: String)(implicit messages: Messages) {

    def nameQuestion(query: QuestionPage[Name],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      val format = (x: Name) => HtmlFormat.escape(x.displayFullName)
      question(query, labelKey, format, changeUrl)
    }

    def stringQuestion(query: QuestionPage[String],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, HtmlFormat.escape, changeUrl)
    }

    def yesNoQuestion(query: QuestionPage[Boolean],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.yesOrNo, changeUrl)
    }

    def dateQuestion(query: QuestionPage[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatDate, changeUrl)
    }

    def ninoQuestion(query: QuestionPage[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatNino, changeUrl)
    }

    def addressQuestion[T <: Address](query: QuestionPage[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatAddress, changeUrl)
    }

    def passportDetailsQuestion(query: QuestionPage[Passport],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatPassportDetails, changeUrl)
    }

    def idCardDetailsQuestion(query: QuestionPage[IdCard],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatIdCardDetails, changeUrl)
    }

    def passportOrIdCardDetailsQuestion(query: QuestionPage[CombinedPassportOrIdCard],
                                        labelKey: String,
                                        changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatPassportOrIdCardDetails, changeUrl)
    }

    def enumQuestion[T](query: QuestionPage[T],
                        labelKey: String,
                        changeUrl: String)
                       (implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {
      val format = (x: T) => HtmlFormat.escape(messages(s"$labelKey.$x"))
      question(query, labelKey, format, changeUrl)
    }

    def conditionalStringQuestion(query: QuestionPage[String],
                                  condition: QuestionPage[Boolean],
                                  labelKey: (String, String),
                                  changeUrl: (String, String)): Option[AnswerRow] = {

      userAnswers.get(condition) match {
        case Some(true) =>
          stringQuestion(query, labelKey._1, changeUrl._1)
        case Some(false) =>
          stringQuestion(query, labelKey._2, changeUrl._2)
        case _ =>
          None
      }
    }

    private def question[T](query: QuestionPage[T],
                            labelKey: String,
                            format: T => Html,
                            changeUrl: String)
                           (implicit rds: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          label = messages(s"$labelKey.checkYourAnswersLabel", name),
          answer = format(x),
          changeUrl = changeUrl
        )
      }
    }
  }
}
