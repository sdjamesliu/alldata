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

package org.apache.druid.indexing.overlord;

import com.google.common.base.Predicate;
import org.apache.druid.guice.annotations.PublicApi;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.config.WorkerTaskRunnerConfig;
import org.apache.druid.indexing.worker.Worker;

import java.util.Collection;

@PublicApi
public interface WorkerTaskRunner extends TaskRunner
{
  enum ActionType
  {
    ENABLE,
    DISABLE
  }

  /**
   * List of known workers who can accept tasks for running
   */
  Collection<ImmutableWorkerInfo> getWorkers();

  /**
   * Return a list of workers who can be reaped by autoscaling
   */
  Collection<Worker> getLazyWorkers();

  /**
   * Check which workers can be marked as lazy
   */
  Collection<Worker> markWorkersLazy(Predicate<ImmutableWorkerInfo> isLazyWorker, int maxWorkers);

  WorkerTaskRunnerConfig getConfig();

  Collection<Task> getPendingTaskPayloads();

}
