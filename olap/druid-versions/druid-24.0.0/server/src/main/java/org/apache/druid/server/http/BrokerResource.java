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

package org.apache.druid.server.http;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.ResourceFilters;
import org.apache.druid.client.BrokerServerView;
import org.apache.druid.server.http.security.StateResourceFilter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/druid/broker/v1")
public class BrokerResource
{
  private final BrokerServerView brokerServerView;

  @Inject
  public BrokerResource(BrokerServerView brokerServerView)
  {
    this.brokerServerView = brokerServerView;
  }

  @GET
  @Path("/loadstatus")
  @ResourceFilters(StateResourceFilter.class)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLoadStatus()
  {
    return Response.ok(ImmutableMap.of("inventoryInitialized", brokerServerView.isInitialized())).build();
  }

  @GET
  @Path("/readiness")
  public Response getReadiness()
  {
    if (brokerServerView.isInitialized()) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
  }
}
