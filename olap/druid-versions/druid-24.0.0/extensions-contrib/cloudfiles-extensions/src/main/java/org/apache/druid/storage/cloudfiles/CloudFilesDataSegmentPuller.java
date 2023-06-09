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

import com.google.inject.Inject;
import org.apache.druid.java.util.common.FileUtils;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.apache.druid.utils.CompressionUtils;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.io.File;
import java.io.IOException;

public class CloudFilesDataSegmentPuller
{

  private static final Logger log = new Logger(CloudFilesDataSegmentPuller.class);
  private final CloudFilesApi cloudFilesApi;

  @Inject
  public CloudFilesDataSegmentPuller(final CloudFilesApi cloudFilesApi)
  {
    this.cloudFilesApi = cloudFilesApi;
  }

  FileUtils.FileCopyResult getSegmentFiles(String region, String container, String path, File outDir)
      throws SegmentLoadingException
  {
    CloudFilesObjectApiProxy objectApi = new CloudFilesObjectApiProxy(cloudFilesApi, region, container);
    final CloudFilesByteSource byteSource = new CloudFilesByteSource(objectApi, path);

    try {
      final FileUtils.FileCopyResult result = CompressionUtils.unzip(
          byteSource,
          outDir,
          CloudFilesUtils.CLOUDFILESRETRY,
          false
      );
      log.info("Loaded %d bytes from [%s] to [%s]", result.size(), path, outDir.getAbsolutePath());
      return result;
    }
    catch (Exception e) {
      try {
        FileUtils.deleteDirectory(outDir);
      }
      catch (IOException ioe) {
        log.warn(
            ioe,
            "Failed to remove output directory [%s] for segment pulled from [%s]",
            outDir.getAbsolutePath(),
            path
        );
      }
      throw new SegmentLoadingException(e, e.getMessage());
    }
    finally {
      try {
        byteSource.closeStream();
      }
      catch (IOException ioe) {
        log.warn(ioe, "Failed to close payload for segmente pulled from [%s]", path);
      }
    }
  }
}
