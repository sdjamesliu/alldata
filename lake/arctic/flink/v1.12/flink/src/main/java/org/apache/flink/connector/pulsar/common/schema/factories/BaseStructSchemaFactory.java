/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connector.pulsar.common.schema.factories;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.common.schema.PulsarSchema;
import org.apache.flink.connector.pulsar.common.schema.PulsarSchemaFactory;
import org.apache.flink.connector.pulsar.common.schema.PulsarSchemaTypeInformation;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaInfo;

import static org.apache.flink.connector.pulsar.common.schema.PulsarSchemaUtils.decodeClassInfo;

/** Implement the common createTypeInfo method for all struct schema factory. */
public abstract class BaseStructSchemaFactory<T> implements PulsarSchemaFactory<T> {

    @Override
    public TypeInformation<T> createTypeInfo(SchemaInfo info) {
        Schema<T> pulsarSchema = createSchema(info);
        Class<T> typeClass = decodeClassInfo(info);
        PulsarSchema<T> schema = new PulsarSchema<>(pulsarSchema, typeClass);

        return new PulsarSchemaTypeInformation<>(schema);
    }
}
