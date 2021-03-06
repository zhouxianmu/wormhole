/*-
 * <<
 * wormhole
 * ==
 * Copyright (C) 2016 - 2017 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */


package edp.rider.rest.persistence.dal

import edp.rider.common.RiderLogger
import edp.rider.rest.persistence.base.BaseDalImpl
import edp.rider.rest.persistence.entities._
import edp.rider.rest.util.CommonUtils._
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await

class StreamUserDefinedTopicDal(udfTopicQuery: TableQuery[StreamUserDefinedTopicTable],
                                streamQuery: TableQuery[StreamTable],
                                instanceQuery: TableQuery[InstanceTable]) extends BaseDalImpl[StreamUserDefinedTopicTable, StreamUserDefinedTopic](udfTopicQuery) with RiderLogger {

  // query stream_intopic table
  def checkUdfTopicExists(streamId: Long, topic: String): Boolean = {
    var exist = false
    if (Await.result(super.findByFilter(udfTopic => udfTopic.streamId === streamId && udfTopic.topic === topic), minTimeOut).nonEmpty)
      exist = true
    exist
  }

  def getUdfTopics(streamId: Long): Seq[StreamTopicTemp] = {
    Await.result(super.findByFilter(_.streamId === streamId), minTimeOut).map(topic => StreamTopicTemp(topic.id, topic.streamId, topic.topic, topic.partitionOffsets, topic.rate))
  }

  def getUdfTopics(streamIds: Seq[Long]): Seq[StreamTopicTemp] = {
    Await.result(super.findByFilter(_.streamId inSet streamIds), minTimeOut).map(topic => StreamTopicTemp(topic.id, topic.streamId, topic.topic, topic.partitionOffsets, topic.rate))
  }

  def getUdfTopicsMap(streamId: Long): Map[Long, String] = {
    getUdfTopics(streamId).map(topic => (topic.id, topic.name)).toMap
  }
}
