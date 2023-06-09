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

package org.apache.druid.msq.indexing.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;

@JsonTypeName(TooManyBucketsFault.CODE)
public class TooManyBucketsFault extends BaseMSQFault
{
  static final String CODE = "TooManyBuckets";

  private final int maxBuckets;

  @JsonCreator
  public TooManyBucketsFault(@JsonProperty("maxBuckets") final int maxBuckets)
  {
    // Currently, partition buckets are only used for segmentGranularity during ingestion queries. So it's fair
    // to assume that a TooManyBuckets error happened due to a too-fine segmentGranularity, even though we don't
    // technically have proof of that.
    super(
        CODE,
        "Too many partition buckets (max = %,d); try breaking your query up into smaller queries or "
        + "using a wider segmentGranularity",
        maxBuckets
    );
    this.maxBuckets = maxBuckets;
  }

  @JsonProperty
  public int getMaxBuckets()
  {
    return maxBuckets;
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
    if (!super.equals(o)) {
      return false;
    }
    TooManyBucketsFault that = (TooManyBucketsFault) o;
    return maxBuckets == that.maxBuckets;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), maxBuckets);
  }
}
