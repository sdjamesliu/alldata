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

package org.apache.druid.timeline.partition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.druid.data.input.StringTuple;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;

/**
 * See {@link BucketNumberedShardSpec} for how this class is used.
 *
 * @see BuildingSingleDimensionShardSpec
 */
public class SingleDimensionRangeBucketShardSpec extends BaseDimensionRangeShardSpec
    implements BucketNumberedShardSpec<BuildingSingleDimensionShardSpec>
{
  private final int bucketId;
  private final String dimension;
  @Nullable
  private final String start;
  @Nullable
  private final String end;

  @JsonCreator
  public SingleDimensionRangeBucketShardSpec(
      @JsonProperty("bucketId") int bucketId,
      @JsonProperty("dimension") String dimension,
      @JsonProperty("start") @Nullable String start,
      @JsonProperty("end") @Nullable String end
  )
  {
    super(
        dimension == null ? Collections.emptyList() : Collections.singletonList(dimension),
        start == null ? null : StringTuple.create(start),
        end == null ? null : StringTuple.create(end)
    );
    this.bucketId = bucketId;
    this.dimension = dimension;
    this.start = start;
    this.end = end;
  }

  @Override
  @JsonProperty
  public int getBucketId()
  {
    return bucketId;
  }

  @JsonProperty
  public String getDimension()
  {
    return dimension;
  }

  @Nullable
  @JsonProperty
  public String getStart()
  {
    return start;
  }

  @Nullable
  @JsonProperty
  public String getEnd()
  {
    return end;
  }

  @Override
  public BuildingSingleDimensionShardSpec convert(int partitionId)
  {
    return new BuildingSingleDimensionShardSpec(bucketId, dimension, start, end, partitionId);
  }

  @Override
  public String getType()
  {
    return Type.BUCKET_SINGLE_DIM;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SingleDimensionRangeBucketShardSpec bucket = (SingleDimensionRangeBucketShardSpec) o;
    return bucketId == bucket.bucketId &&
           Objects.equals(dimension, bucket.dimension) &&
           Objects.equals(start, bucket.start) &&
           Objects.equals(end, bucket.end);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(bucketId, dimension, start, end);
  }

  @Override
  public String toString()
  {
    return "SingleDimensionRangeBucketShardSpec{" +
           ", bucketId=" + bucketId +
           ", dimension='" + dimension + '\'' +
           ", start='" + start + '\'' +
           ", end='" + end + '\'' +
           '}';
  }
}
