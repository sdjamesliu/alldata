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

package org.apache.druid.indexer.partitions;

import javax.annotation.Nullable;
import java.util.List;

/**
 * PartitionsSpec based on dimension values.
 */
public interface DimensionBasedPartitionsSpec extends PartitionsSpec
{
  String PARTITION_DIMENSIONS = "partitionDimensions";

  String TARGET_ROWS_PER_SEGMENT = "targetRowsPerSegment";
  String MAX_PARTITION_SIZE = "maxPartitionSize";
  String ASSUME_GROUPED = "assumeGrouped";

  /**
   * Message denoting that this spec is forceGuaranteedRollup compatible.
   */
  String FORCE_GUARANTEED_ROLLUP_COMPATIBLE = "";

  // Deprecated properties preserved for backward compatibility:
  @Deprecated
  String TARGET_PARTITION_SIZE = "targetPartitionSize";

  List<String> getPartitionDimensions();

  @Nullable
  Integer getTargetRowsPerSegment();
}
