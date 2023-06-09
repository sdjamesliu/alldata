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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.segment.SegmentUtils;
import org.apache.druid.segment.loading.DataSegmentPusher;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.utils.CompressionUtils;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

public class CloudFilesDataSegmentPusher implements DataSegmentPusher
{

  private static final Logger log = new Logger(CloudFilesDataSegmentPusher.class);
  private final CloudFilesObjectApiProxy objectApi;
  private final CloudFilesDataSegmentPusherConfig config;
  private final ObjectMapper jsonMapper;

  @Inject
  public CloudFilesDataSegmentPusher(
      final CloudFilesApi cloudFilesApi,
      final CloudFilesDataSegmentPusherConfig config, final ObjectMapper jsonMapper
  )
  {
    this.config = config;
    String region = this.config.getRegion();
    String container = this.config.getContainer();
    this.objectApi = new CloudFilesObjectApiProxy(cloudFilesApi, region, container);
    this.jsonMapper = jsonMapper;
  }

  @Override
  public String getPathForHadoop()
  {
    return null;
  }

  @Deprecated
  @Override
  public String getPathForHadoop(final String dataSource)
  {
    return getPathForHadoop();
  }

  @Override
  public DataSegment push(final File indexFilesDir, final DataSegment inSegment, final boolean useUniquePath)
  {
    return pushToPath(indexFilesDir, inSegment, getStorageDir(inSegment, useUniquePath));
  }

  @Override
  public DataSegment pushToPath(File indexFilesDir, DataSegment inSegment, String storageDirSuffix)
  {
    final String segmentPath = CloudFilesUtils.buildCloudFilesPath(
        this.config.getBasePath(),
        storageDirSuffix
        );
    File descriptorFile = null;
    File zipOutFile = null;

    try {
      final File descFile = descriptorFile = File.createTempFile("descriptor", ".json");
      final File outFile = zipOutFile = File.createTempFile("druid", "index.zip");

      final long indexSize = CompressionUtils.zip(indexFilesDir, zipOutFile);

      log.info("Copying segment[%s] to CloudFiles at location[%s]", inSegment.getId(), segmentPath);
      return CloudFilesUtils.retryCloudFilesOperation(
          () -> {
            CloudFilesObject segmentData = new CloudFilesObject(
                segmentPath,
                outFile,
                objectApi.getRegion(),
                objectApi.getContainer()
            );

            log.info("Pushing %s.", segmentData.getPath());
            objectApi.put(segmentData);

            // Avoid using Guava in DataSegmentPushers because they might be used with very diverse Guava versions in
            // runtime, and because Guava deletes methods over time, that causes incompatibilities.
            Files.write(descFile.toPath(), jsonMapper.writeValueAsBytes(inSegment));
            CloudFilesObject descriptorData = new CloudFilesObject(
                segmentPath,
                descFile,
                objectApi.getRegion(),
                objectApi.getContainer()
            );
            log.info("Pushing %s.", descriptorData.getPath());
            objectApi.put(descriptorData);

            final DataSegment outSegment = inSegment
                .withSize(indexSize)
                .withLoadSpec(makeLoadSpec(new URI(segmentData.getPath())))
                .withBinaryVersion(SegmentUtils.getVersionFromDir(indexFilesDir));

            return outSegment;
          },
          this.config.getOperationMaxRetries()
      );
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      if (zipOutFile != null) {
        log.info("Deleting zipped index File[%s]", zipOutFile);
        zipOutFile.delete();
      }

      if (descriptorFile != null) {
        log.info("Deleting descriptor file[%s]", descriptorFile);
        descriptorFile.delete();
      }
    }
  }

  @Override
  public Map<String, Object> makeLoadSpec(URI uri)
  {
    return ImmutableMap.of(
        "type",
        CloudFilesStorageDruidModule.SCHEME,
        "region",
        objectApi.getRegion(),
        "container",
        objectApi.getContainer(),
        "path",
        uri.toString()
    );
  }
}
