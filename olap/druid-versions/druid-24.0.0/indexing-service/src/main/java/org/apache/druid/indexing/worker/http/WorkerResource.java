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

package org.apache.druid.indexing.worker.http;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.spi.container.ResourceFilters;
import org.apache.druid.common.utils.IdUtils;
import org.apache.druid.curator.ZkEnablementConfig;
import org.apache.druid.indexing.overlord.TaskRunner;
import org.apache.druid.indexing.overlord.TaskRunnerWorkItem;
import org.apache.druid.indexing.worker.Worker;
import org.apache.druid.indexing.worker.WorkerCuratorCoordinator;
import org.apache.druid.indexing.worker.WorkerTaskManager;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.server.http.HttpMediaType;
import org.apache.druid.server.http.security.ConfigResourceFilter;
import org.apache.druid.server.http.security.StateResourceFilter;
import org.apache.druid.tasklogs.TaskLogStreamer;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Path("/druid/worker/v1")
public class WorkerResource
{
  private static final Logger log = new Logger(WorkerResource.class);
  private static String DISABLED_VERSION = "";

  private final Worker enabledWorker;

  @Nullable // Null, if zk is disabled
  private final WorkerCuratorCoordinator curatorCoordinator;

  private final TaskRunner taskRunner;
  private final WorkerTaskManager workerTaskManager;

  @Inject
  public WorkerResource(
      Worker worker,
      Provider<WorkerCuratorCoordinator> curatorCoordinatorProvider,
      TaskRunner taskRunner,
      WorkerTaskManager workerTaskManager,
      ZkEnablementConfig zkEnablementConfig

  )
  {
    this.enabledWorker = worker;
    this.taskRunner = taskRunner;
    this.workerTaskManager = workerTaskManager;

    if (zkEnablementConfig.isEnabled()) {
      this.curatorCoordinator = curatorCoordinatorProvider.get();
    } else {
      this.curatorCoordinator = null;
    }
  }


  @POST
  @Path("/disable")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceFilters(ConfigResourceFilter.class)
  public Response doDisable()
  {
    try {
      if (curatorCoordinator != null) {
        final Worker disabledWorker = new Worker(
            enabledWorker.getScheme(),
            enabledWorker.getHost(),
            enabledWorker.getIp(),
            enabledWorker.getCapacity(),
            DISABLED_VERSION,
            enabledWorker.getCategory()
        );
        curatorCoordinator.updateWorkerAnnouncement(disabledWorker);
      }
      workerTaskManager.workerDisabled();
      return Response.ok(ImmutableMap.of(enabledWorker.getHost(), "disabled")).build();
    }
    catch (Exception e) {
      return Response.serverError().build();
    }
  }

  @POST
  @Path("/enable")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceFilters(ConfigResourceFilter.class)
  public Response doEnable()
  {
    try {
      if (curatorCoordinator != null) {
        curatorCoordinator.updateWorkerAnnouncement(enabledWorker);
      }
      workerTaskManager.workerEnabled();
      return Response.ok(ImmutableMap.of(enabledWorker.getHost(), "enabled")).build();
    }
    catch (Exception e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/enabled")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceFilters(StateResourceFilter.class)
  public Response isEnabled()
  {
    try {
      return Response.ok(ImmutableMap.of(enabledWorker.getHost(), workerTaskManager.isWorkerEnabled())).build();
    }
    catch (Exception e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/tasks")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceFilters(StateResourceFilter.class)
  public Response getTasks()
  {
    try {
      return Response.ok(
          Lists.newArrayList(
              Collections2.transform(
                  taskRunner.getKnownTasks(),
                  new Function<TaskRunnerWorkItem, String>()
                  {
                    @Override
                    public String apply(TaskRunnerWorkItem input)
                    {
                      return input.getTaskId();
                    }
                  }
              )
          )
      ).build();
    }
    catch (Exception e) {
      return Response.serverError().build();
    }
  }

  @POST
  @Path("/task/{taskid}/shutdown")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceFilters(StateResourceFilter.class)
  public Response doShutdown(@PathParam("taskid") String taskid)
  {
    try {
      taskRunner.shutdown(taskid, "shut down request via HTTP endpoint");
    }
    catch (Exception e) {
      log.error(e, "Failed to issue shutdown for task: %s", taskid);
      return Response.serverError().build();
    }
    return Response.ok(ImmutableMap.of("task", taskid)).build();
  }

  @GET
  @Path("/task/{taskid}/log")
  @Produces(HttpMediaType.TEXT_PLAIN_UTF8)
  @ResourceFilters(StateResourceFilter.class)
  public Response doGetLog(
      @PathParam("taskid") String taskId,
      @QueryParam("offset") @DefaultValue("0") long offset
  )
  {
    IdUtils.validateId("taskId", taskId);
    if (!(taskRunner instanceof TaskLogStreamer)) {
      return Response.status(501)
                     .type(MediaType.TEXT_PLAIN)
                     .entity(StringUtils.format(
                         "Log streaming not supported by [%s]",
                         taskRunner.getClass().getName()
                     ))
                     .build();
    }
    try {
      final Optional<InputStream> stream = ((TaskLogStreamer) taskRunner).streamTaskLog(taskId, offset);

      if (stream.isPresent()) {
        return Response.ok(stream.get()).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
    catch (IOException e) {
      log.warn(e, "Failed to read log for task: %s", taskId);
      return Response.serverError().build();
    }
  }
}
