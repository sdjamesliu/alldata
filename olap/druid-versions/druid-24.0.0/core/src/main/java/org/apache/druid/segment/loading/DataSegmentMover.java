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

package org.apache.druid.segment.loading;

import org.apache.druid.guice.annotations.ExtensionPoint;
import org.apache.druid.timeline.DataSegment;

import java.util.Map;

/**
 * DataSegmentMover knows how to move the segment location from one to another.
 * Since any implementation of DataSegmentMover is initialized when an ingestion job starts
 * if a deep storage extension is loaded even when that deep storage is actually not used,
 * implementations should avoid initializing the deep storage client immediately
 * but defer it until the deep storage client is actually used.
 */
@ExtensionPoint
public interface DataSegmentMover
{
  DataSegment move(DataSegment segment, Map<String, Object> targetLoadSpec) throws SegmentLoadingException;
}
