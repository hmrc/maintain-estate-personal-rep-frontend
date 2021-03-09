/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDateTime

import javax.inject.Inject
import models.UserAnswers
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject()(
                                          val mongo: ReactiveMongoApi,
                                          val config: Configuration
                                        )(implicit val ec: ExecutionContext)
  extends SessionRepository
    with IndexManager {

  override val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] = for {
    _ <- ensureIndexes
    col <- mongo.database.map(_.collection[JSONCollection](collectionName))
  } yield col

  private val lastUpdatedIndex = MongoIndex(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = "user-answers-last-updated-index",
    expireAfterSeconds = Some(cacheTtl)
  )

  private def ensureIndexes: Future[Unit] = for {
    col <- mongo.database.map(_.collection[JSONCollection](collectionName))
    _ <- col.indexesManager.ensure(lastUpdatedIndex)
  } yield ()

  override def get(id: String): Future[Option[UserAnswers]] =
    collection.flatMap(_.find(Json.obj("_id" -> id), None).one[UserAnswers])

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    collection.flatMap {
      _.update(ordered = false)
        .one(selector, modifier, upsert = true).map {
        lastError =>
          lastError.ok
      }
    }
  }
}

trait SessionRepository {

  def get(id: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
