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

package org.apache.druid.server;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.server.initialization.ServerConfig;


public class QuerySchedulerProvider extends QuerySchedulerConfig implements Provider<QueryScheduler>
{
  private final ServerConfig serverConfig;
  private final ServiceEmitter emitter;

  /**
   * This needs to be both marked as guice injected to be bound correctly, and also marked with json creator and
   * jackson inject to work with {@link org.apache.druid.guice.JsonConfigProvider}
   */
  @Inject
  @JsonCreator
  public QuerySchedulerProvider(@JacksonInject ServerConfig serverConfig, @JacksonInject ServiceEmitter emitter)
  {
    this.serverConfig = serverConfig;
    this.emitter = emitter;
  }

  @Override
  public QueryScheduler get()
  {
    return new QueryScheduler(getNumThreads(), getPrioritizationStrategy(), getLaningStrategy(), serverConfig, emitter);
  }
}
