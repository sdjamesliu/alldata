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

package org.apache.druid.storage.cloudfiles;

import com.google.common.io.Files;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.easymock.EasyMock;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 */
public class CloudFilesDataSegmentPusherTest
{
  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testPush() throws Exception
  {
    ObjectApi objectApi = EasyMock.createStrictMock(ObjectApi.class);
    EasyMock.expect(objectApi.put(EasyMock.anyString(), EasyMock.anyObject())).andReturn(null).atLeastOnce();
    EasyMock.replay(objectApi);

    CloudFilesApi api = EasyMock.createStrictMock(CloudFilesApi.class);
    EasyMock.expect(api.getObjectApi(EasyMock.anyString(), EasyMock.anyString()))
            .andReturn(objectApi)
            .atLeastOnce();
    EasyMock.replay(api);


    CloudFilesDataSegmentPusherConfig config = new CloudFilesDataSegmentPusherConfig();
    config.setRegion("region");
    config.setContainer("container");
    config.setBasePath("basePath");

    CloudFilesDataSegmentPusher pusher = new CloudFilesDataSegmentPusher(api, config, new DefaultObjectMapper());

    // Create a mock segment on disk
    File tmp = tempFolder.newFile("version.bin");

    final byte[] data = new byte[]{0x0, 0x0, 0x0, 0x1};
    Files.write(data, tmp);
    final long size = data.length;

    DataSegment segmentToPush = new DataSegment(
        "foo",
        Intervals.of("2015/2016"),
        "0",
        new HashMap<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        NoneShardSpec.instance(),
        0,
        size
    );

    DataSegment segment = pusher.push(tempFolder.getRoot(), segmentToPush, false);

    Assert.assertEquals(segmentToPush.getSize(), segment.getSize());

    EasyMock.verify(api);
  }
}
