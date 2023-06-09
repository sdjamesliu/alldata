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

package org.apache.druid.indexing.overlord.setup;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;
import org.apache.druid.guice.annotations.PublicApi;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.ImmutableWorkerInfo;
import org.apache.druid.indexing.overlord.config.WorkerTaskRunnerConfig;

import javax.annotation.Nullable;

/**
 * The {@link org.apache.druid.indexing.overlord.RemoteTaskRunner} uses this class to select a worker to assign tasks to.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = EqualDistributionWorkerSelectStrategy.class)
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "fillCapacity", value = FillCapacityWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "fillCapacityWithAffinity", value = FillCapacityWithAffinityWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "equalDistribution", value = EqualDistributionWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "equalDistributionWithAffinity", value = EqualDistributionWithAffinityWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "javascript", value = JavaScriptWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "fillCapacityWithCategorySpec", value = FillCapacityWithCategorySpecWorkerSelectStrategy.class),
    @JsonSubTypes.Type(name = "equalDistributionWithCategorySpec", value = EqualDistributionWithCategorySpecWorkerSelectStrategy.class)
})
@PublicApi
public interface WorkerSelectStrategy
{
  /**
   * Customizable logic for selecting a worker to run a task.
   *
   * @param config    A config for running remote tasks
   * @param zkWorkers An immutable map of workers to choose from.
   * @param task      The task to assign.
   *
   * @return A {@link ImmutableWorkerInfo} to run the task if one is available.
   */
  @Nullable
  ImmutableWorkerInfo findWorkerForTask(
      WorkerTaskRunnerConfig config,
      ImmutableMap<String, ImmutableWorkerInfo> zkWorkers,
      Task task
  );
}
