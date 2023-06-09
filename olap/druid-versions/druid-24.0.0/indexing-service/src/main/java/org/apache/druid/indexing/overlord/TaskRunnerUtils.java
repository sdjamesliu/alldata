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

import com.google.common.base.Preconditions;
import org.apache.druid.indexer.TaskLocation;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.worker.Worker;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.emitter.EmittingLogger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Executor;

public class TaskRunnerUtils
{
  private static final EmittingLogger log = new EmittingLogger(TaskRunnerUtils.class);

  public static void notifyLocationChanged(
      final Iterable<Pair<TaskRunnerListener, Executor>> listeners,
      final String taskId,
      final TaskLocation location
  )
  {
    log.debug("Task [%s] location changed to [%s].", taskId, location);
    for (final Pair<TaskRunnerListener, Executor> listener : listeners) {
      try {
        listener.rhs.execute(() -> listener.lhs.locationChanged(taskId, location));
      }
      catch (Exception e) {
        log.makeAlert(e, "Unable to notify task listener")
           .addData("taskId", taskId)
           .addData("taskLocation", location)
           .addData("listener", listener.toString())
           .emit();
      }
    }
  }

  public static void notifyStatusChanged(
      final Iterable<Pair<TaskRunnerListener, Executor>> listeners,
      final String taskId,
      final TaskStatus status
  )
  {
    log.debug("Task [%s] status changed to [%s].", taskId, status.getStatusCode());
    for (final Pair<TaskRunnerListener, Executor> listener : listeners) {
      try {
        listener.rhs.execute(() -> listener.lhs.statusChanged(taskId, status));
      }
      catch (Exception e) {
        log.makeAlert(e, "Unable to notify task listener")
           .addData("taskId", taskId)
           .addData("taskStatus", status.getStatusCode())
           .addData("listener", listener.toString())
           .emit();
      }
    }
  }

  public static URL makeWorkerURL(Worker worker, String pathFormat, String... pathParams)
  {
    Preconditions.checkArgument(pathFormat.startsWith("/"), "path must start with '/': %s", pathFormat);
    final String path = StringUtils.format(
        pathFormat,
        Arrays.stream(pathParams).map(StringUtils::urlEncode).toArray()
    );

    try {
      return new URI(StringUtils.format("%s://%s%s", worker.getScheme(), worker.getHost(), path)).toURL();
    }
    catch (URISyntaxException | MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public static URL makeTaskLocationURL(TaskLocation taskLocation, String pathFormat, String... pathParams)
  {
    Preconditions.checkArgument(pathFormat.startsWith("/"), "path must start with '/': %s", pathFormat);
    final String path = StringUtils.format(
        pathFormat,
        Arrays.stream(pathParams).map(StringUtils::urlEncode).toArray()
    );

    try {
      return taskLocation.makeURL(path);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
