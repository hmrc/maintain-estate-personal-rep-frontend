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

package utils.print

import java.time.LocalDate

import com.google.inject.Inject
import models.{Address, IdCard, Name, Passport, UserAnswers}
import pages.QuestionPage
import pages.business.NamePage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.countryOptions.CountryOptions
import utils.print.CheckAnswersFormatters.{formatIdCardDetails, formatNino, formatPassportDetails, _}
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()() {

  def bind(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name, countryOptions)

  class Bound(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)(implicit messages: Messages) {

    def nameQuestion(query: QuestionPage[Name],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
          HtmlFormat.escape(x.displayFullName),
          changeUrl
        )
      }
    }

    def stringQuestion(query: QuestionPage[String],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(x),
          changeUrl
        )
      }
    }

    def yesNoQuestion(query: QuestionPage[Boolean],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          yesOrNo(x),
          changeUrl
        )
      }
    }

    def dateQuestion(query: QuestionPage[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(x.format(dateFormatter)),
          changeUrl
        )
      }
    }

    def ninoQuestion(query: QuestionPage[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          formatNino(x),
          changeUrl
        )
      }
    }

    def addressQuestion[T <: Address](query: QuestionPage[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          formatAddress(x, countryOptions),
          changeUrl
        )
      }
    }

    def passportDetailsQuestion(query: QuestionPage[Passport],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          formatPassportDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def idCardDetailsQuestion(query: QuestionPage[IdCard],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          formatIdCardDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def enumQuestion[T](query: QuestionPage[T],
                        labelKey: String,
                        changeUrl: String)
                       (implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {

      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(messages(s"$labelKey.$x")),
          changeUrl
        )
      }
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
  }
}
