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

package org.apache.druid.sql.calcite.schema;

import com.google.inject.Inject;
import org.apache.calcite.schema.Schema;
import org.apache.druid.server.security.ResourceType;

/**
 * The schema for Druid tables to be accessible via SQL.
 */
public class NamedDruidSchema implements NamedSchema
{
  private final DruidSchema druidSchema;
  private final String druidSchemaName;

  @Inject
  public NamedDruidSchema(DruidSchema druidSchema, @DruidSchemaName String druidSchemaName)
  {
    this.druidSchema = druidSchema;
    this.druidSchemaName = druidSchemaName;
  }

  @Override
  public String getSchemaName()
  {
    return druidSchemaName;
  }

  @Override
  public String getSchemaResourceType(String resourceName)
  {
    return ResourceType.DATASOURCE;
  }

  @Override
  public Schema getSchema()
  {
    return druidSchema;
  }
}
