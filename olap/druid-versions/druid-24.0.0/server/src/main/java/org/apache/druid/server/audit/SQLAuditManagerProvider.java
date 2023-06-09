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

package org.apache.druid.server.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import org.apache.druid.audit.AuditManager;
import org.apache.druid.guice.annotations.Json;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.metadata.MetadataStorageTablesConfig;
import org.apache.druid.metadata.SQLMetadataConnector;

public class SQLAuditManagerProvider implements AuditManagerProvider
{
  private final Supplier<MetadataStorageTablesConfig> dbTables;
  private final SQLMetadataConnector connector;
  private final Lifecycle lifecycle;
  private final ServiceEmitter emitter;
  private final ObjectMapper mapper;
  private final SQLAuditManagerConfig config;

  @Inject
  public SQLAuditManagerProvider(
      Supplier<MetadataStorageTablesConfig> dbTables,
      SQLMetadataConnector connector,
      Lifecycle lifecycle,
      ServiceEmitter emitter,
      @Json ObjectMapper mapper,
      SQLAuditManagerConfig config
  )
  {
    this.dbTables = dbTables;
    this.connector = connector;
    this.lifecycle = lifecycle;
    this.emitter = emitter;
    this.mapper = mapper;
    this.config = config;
  }

  @Override
  public AuditManager get()
  {
    try {
      lifecycle.addMaybeStartHandler(
          new Lifecycle.Handler()
          {
            @Override
            public void start()
            {
              connector.createAuditTable();
            }

            @Override
            public void stop()
            {

            }
          }
      );
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new SQLAuditManager(connector, dbTables, emitter, mapper, config);
  }
}
