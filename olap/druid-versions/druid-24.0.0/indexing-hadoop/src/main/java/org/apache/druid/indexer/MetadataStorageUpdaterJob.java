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

package org.apache.druid.indexer;

import org.apache.druid.timeline.DataSegment;

import java.util.List;
import java.util.stream.Collectors;

/**
 */
public class MetadataStorageUpdaterJob implements Jobby
{
  private final HadoopDruidIndexerConfig config;
  private final MetadataStorageUpdaterJobHandler handler;

  public MetadataStorageUpdaterJob(
      HadoopDruidIndexerConfig config,
      MetadataStorageUpdaterJobHandler handler
  )
  {
    this.config = config;
    this.handler = handler;
  }

  @Override
  public boolean run()
  {
    final List<DataSegmentAndIndexZipFilePath> segmentAndIndexZipFilePaths = IndexGeneratorJob.getPublishedSegmentAndIndexZipFilePaths(config);
    final List<DataSegment> segments = segmentAndIndexZipFilePaths.stream().map(s -> s.getSegment()).collect(Collectors.toList());
    final String segmentTable = config.getSchema().getIOConfig().getMetadataUpdateSpec().getSegmentTable();
    handler.publishSegments(segmentTable, segments, HadoopDruidIndexerConfig.JSON_MAPPER);

    return true;
  }
}
