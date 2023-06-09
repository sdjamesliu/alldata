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

package org.apache.druid.discovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.server.coordination.ServerType;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class DataNodeServiceTest
{
  private final ObjectMapper mapper = DruidServiceTestUtils.newJsonMapper();

  @Test
  public void testSerde() throws Exception
  {
    DruidService expected = new DataNodeService(
        "tier",
        100,
        ServerType.HISTORICAL,
        1
    );

    DruidService actual = mapper.readValue(
        mapper.writeValueAsString(expected),
        DruidService.class
    );

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testDeserializeWithDeprecatedServerTypeProperty() throws Exception
  {
    String json = "{\n"
                  + "  \"type\" : \"dataNodeService\",\n"
                  + "  \"tier\" : \"tier\",\n"
                  + "  \"maxSize\" : 100,\n"
                  + "  \"type\" : \"historical\",\n"
                  + "  \"priority\" : 1\n"
                  + "}";

    DruidService actual = mapper.readValue(
        json,
        DruidService.class
    );

    Assert.assertEquals(
        new DataNodeService(
            "tier",
            100,
            ServerType.HISTORICAL,
            1
        ),
        actual
    );
  }

  @Test
  public void testDeserializeWithServerTypeProperty() throws Exception
  {
    String json = "{\n"
                  + "  \"type\" : \"dataNodeService\",\n"
                  + "  \"tier\" : \"tier\",\n"
                  + "  \"maxSize\" : 100,\n"
                  + "  \"serverType\" : \"historical\",\n"
                  + "  \"priority\" : 1\n"
                  + "}";

    DruidService actual = mapper.readValue(
        json,
        DruidService.class
    );

    Assert.assertEquals(
        new DataNodeService(
            "tier",
            100,
            ServerType.HISTORICAL,
            1
        ),
        actual
    );
  }

  @Test
  public void testSerdeDeserializeWithBothDeprecatedAndNewServerTypes() throws Exception
  {
    String json = "{\n"
                  + "  \"type\" : \"dataNodeService\",\n"
                  + "  \"tier\" : \"tier\",\n"
                  + "  \"maxSize\" : 100,\n"
                  + "  \"type\" : \"historical\",\n"
                  + "  \"serverType\" : \"historical\",\n"
                  + "  \"priority\" : 1\n"
                  + "}";

    DruidService actual = mapper.readValue(
        json,
        DruidService.class
    );

    Assert.assertEquals(
        new DataNodeService(
            "tier",
            100,
            ServerType.HISTORICAL,
            1
        ),
        actual
    );
  }

  @Test
  public void testSerializeSubtypeKeyShouldAppearFirstInJson() throws JsonProcessingException
  {
    final DataNodeService dataNodeService = new DataNodeService(
        "tier",
        100,
        ServerType.HISTORICAL,
        1
    );
    final String json = mapper.writeValueAsString(dataNodeService);
    Assert.assertTrue(json.startsWith(StringUtils.format("{\"type\":\"%s\"", DataNodeService.DISCOVERY_SERVICE_KEY)));
  }
}
