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

package repositories

import com.mongodb.client.model.{FindOneAndUpdateOptions, Indexes, ReturnDocument, Updates}
import models.UserAnswers
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultSessionRepository @Inject()(val mongo: MongoComponent,
                                         val config: Configuration
                                        )(implicit val ec: ExecutionContext)
  extends PlayMongoRepository[UserAnswers](
    mongoComponent = mongo,
    collectionName = "user-answers",
    domainFormat = Format(UserAnswers.reads, UserAnswers.writes),
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions().name("user-answers-last-updated-index")
          .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
          .unique(false))
    ),
    replaceIndexes = config.getOptional[Boolean]("microservice.services.features.mongo.dropIndexes").getOrElse(false)
  ) with SessionRepository {

  private def byId(id: String) = Filters.eq("_id", id)

  override def get(id: String): Future[Option[UserAnswers]] = {
    val modifier = Updates.set("lastUpdated", LocalDateTime.now)

    val options = new FindOneAndUpdateOptions()
      .upsert(false)
      .returnDocument(ReturnDocument.AFTER)
    collection.findOneAndUpdate(byId(id), modifier, options).headOption()
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {
    val selector = byId(userAnswers.id)
    val options =  new ReplaceOptions().upsert(true)

    collection.replaceOne(selector, userAnswers.copy(lastUpdated = LocalDateTime.now), options)
      .headOption()
      .map(_.exists(_.wasAcknowledged()))
  }
}

trait SessionRepository {

  def get(id: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
