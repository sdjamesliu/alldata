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

package org.apache.druid.segment.incremental;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RowIngestionMetersTotals
{
  private final long processed;
  private final long processedWithError;
  private final long thrownAway;
  private final long unparseable;

  @JsonCreator
  public RowIngestionMetersTotals(
      @JsonProperty("processed") long processed,
      @JsonProperty("processedWithError") long processedWithError,
      @JsonProperty("thrownAway") long thrownAway,
      @JsonProperty("unparseable") long unparseable
  )
  {
    this.processed = processed;
    this.processedWithError = processedWithError;
    this.thrownAway = thrownAway;
    this.unparseable = unparseable;
  }

  @JsonProperty
  public long getProcessed()
  {
    return processed;
  }

  @JsonProperty
  public long getProcessedWithError()
  {
    return processedWithError;
  }

  @JsonProperty
  public long getThrownAway()
  {
    return thrownAway;
  }

  @JsonProperty
  public long getUnparseable()
  {
    return unparseable;
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
    RowIngestionMetersTotals that = (RowIngestionMetersTotals) o;
    return processed == that.processed
           && processedWithError == that.processedWithError
           && thrownAway == that.thrownAway
           && unparseable == that.unparseable;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(processed, processedWithError, thrownAway, unparseable);
  }

  @Override
  public String toString()
  {
    return "RowIngestionMetersTotals{" +
           "processed=" + processed +
           ", processedWithError=" + processedWithError +
           ", thrownAway=" + thrownAway +
           ", unparseable=" + unparseable +
           '}';
  }
}
