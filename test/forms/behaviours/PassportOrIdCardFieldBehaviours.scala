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

package forms.behaviours

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

trait PassportOrIdCardFieldBehaviours extends StringFieldBehaviours {

  def dateField(form: Form[_], key: String, validData: Gen[LocalDate]): Unit = {

    "bind valid data" in {

      forAll(validData -> "valid date") {
        date =>

          val data = Map(
            s"$key.day"   -> date.getDayOfMonth.toString,
            s"$key.month" -> date.getMonthValue.toString,
            s"$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.errors.foreach(error =>
            error.key shouldNot equal(key)
          )
      }
    }
  }

  def dateFieldWithMax(form: Form[_], key: String, max: LocalDate, error: FormError): Unit = {

    s"fail to bind a date greater than ${max.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = datesBetween(max.plusDays(1), max.plusYears(10))

      forAll(generator -> "invalid dates") {
        date =>

          val data = Map(
            s"$key.day"   -> date.getDayOfMonth.toString,
            s"$key.month" -> date.getMonthValue.toString,
            s"$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.errors.last shouldEqual error
      }
    }
  }

  def dateFieldWithMin(form: Form[_], key: String, min: LocalDate, error: FormError): Unit = {

    s"fail to bind a date earlier than ${min.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = datesBetween(min.minusYears(10), min.minusDays(1))

      forAll(generator -> "invalid dates") {
        date =>

          val data = Map(
            s"$key.day"   -> date.getDayOfMonth.toString,
            s"$key.month" -> date.getMonthValue.toString,
            s"$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.errors.last shouldEqual error
      }
    }
  }

  def mandatoryDateField(form: Form[_], key: String, requiredAllKey: String, errorArgs: Seq[String] = Seq.empty): Unit = {

    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors.last shouldEqual FormError(key, requiredAllKey, errorArgs)
    }
  }
}
