/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.indexing.overlord.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.druid.indexer.TaskStatusPlus;

import javax.annotation.Nullable;
import java.util.Objects;

public class TaskStatusResponse
{
  private final String task; // Task ID, named "task" in the JSONification of this class.
  @Nullable
  private final TaskStatusPlus status;

  @JsonCreator
  public TaskStatusResponse(
      @JsonProperty("task") final String task,
      @JsonProperty("status") @Nullable final TaskStatusPlus status
  )
  {
    this.task = task;
    this.status = status;
  }

  @JsonProperty
  public String getTask()
  {
    return task;
  }

  @JsonProperty
  @Nullable
  public TaskStatusPlus getStatus()
  {
    return status;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final TaskStatusResponse that = (TaskStatusResponse) o;
    return Objects.equals(task, that.task) &&
           Objects.equals(status, that.status);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(task, status);
  }

  @Override
  public String toString()
  {
    return "TaskstatusResponse{" +
           "task='" + task + '\'' +
           ", status=" + status +
           '}';
  }
}
