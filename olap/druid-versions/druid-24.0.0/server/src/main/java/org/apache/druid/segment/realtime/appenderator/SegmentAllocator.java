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

package org.apache.druid.segment.realtime.appenderator;

import org.apache.druid.data.input.InputRow;

import javax.annotation.Nullable;
import java.io.IOException;

public interface SegmentAllocator
{
  /**
   * Allocates a new segment for a given timestamp. Even though its name is "allocate", this method is actually
   * idempotent: given the same sequenceName, previousSegmentId, and skipSegmentLineageCheck, the implementation
   * must return the same segment ID.
   *
   * @param row                     the event which triggered this allocation request
   * @param sequenceName            sequenceName for this allocation
   * @param previousSegmentId       segment identifier returned on the previous call to allocate for your sequenceName.
   *                                When skipSegmentLineageCheck is false, this can be null if it is the first call
   *                                for the same sequenceName.
   *                                When skipSegmentLineageCheck is true, this will be ignored.
   * @param skipSegmentLineageCheck if false, perform lineage validation using previousSegmentId for this sequence.
   *                                Should be set to false if replica tasks would index events in same order
   *
   * @return the pending segment identifier, or null if it was impossible to allocate a new segment
   */
  @Nullable
  SegmentIdWithShardSpec allocate(
      InputRow row,
      String sequenceName,
      @Nullable String previousSegmentId,
      boolean skipSegmentLineageCheck
  ) throws IOException;
}
