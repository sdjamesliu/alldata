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

package org.apache.druid.query.lookup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.utils.JvmUtils;

import javax.validation.constraints.Min;
import java.util.Objects;

public class LookupConfig
{
  static int DEFAULT_COORDINATOR_RETRY_DELAY = 60_000;

  @JsonProperty("snapshotWorkingDir")
  private String snapshotWorkingDir;

  @JsonProperty("enableLookupSyncOnStartup")
  private boolean enableLookupSyncOnStartup = true;

  @Min(1)
  @JsonProperty("numLookupLoadingThreads")
  private int numLookupLoadingThreads = Math.max(1, JvmUtils.getRuntimeInfo().getAvailableProcessors() / 2);

  @Min(1)
  @JsonProperty("coordinatorFetchRetries")
  private int coordinatorFetchRetries = 3;

  // By default, add an extra minute in addition to the retry wait. In RetryUtils, retry wait starts from a few
  // seconds, that is likely not enough to coordinator to be back to healthy state, e. g. if it experiences
  // 30-second GC pause.
  @Min(0)
  @JsonProperty("coordinatorRetryDelay")
  private int coordinatorRetryDelay = DEFAULT_COORDINATOR_RETRY_DELAY;

  @Min(1)
  @JsonProperty("lookupStartRetries")
  private int lookupStartRetries = 3;

  /**
   * @param snapshotWorkingDir working directory to store lookups snapshot file, passing null or empty string will
   *                           disable the snapshot utility
   */
  @JsonCreator
  public LookupConfig(
      @JsonProperty("snapshotWorkingDir") String snapshotWorkingDir
  )
  {
    this.snapshotWorkingDir = StringUtils.nullToEmptyNonDruidDataString(snapshotWorkingDir);
  }

  public String getSnapshotWorkingDir()
  {
    return snapshotWorkingDir;
  }

  public int getNumLookupLoadingThreads()
  {
    return numLookupLoadingThreads;
  }

  public boolean getEnableLookupSyncOnStartup()
  {
    return enableLookupSyncOnStartup;
  }

  public int getCoordinatorFetchRetries()
  {
    return coordinatorFetchRetries;
  }

  public int getLookupStartRetries()
  {
    return lookupStartRetries;
  }

  public int getCoordinatorRetryDelay()
  {
    return coordinatorRetryDelay;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LookupConfig)) {
      return false;
    }

    LookupConfig that = (LookupConfig) o;

    return Objects.equals(snapshotWorkingDir, that.snapshotWorkingDir) &&
           enableLookupSyncOnStartup == that.enableLookupSyncOnStartup &&
           numLookupLoadingThreads == that.numLookupLoadingThreads &&
           coordinatorFetchRetries == that.coordinatorFetchRetries &&
           lookupStartRetries == that.lookupStartRetries &&
           coordinatorRetryDelay == that.coordinatorRetryDelay;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(
        snapshotWorkingDir,
        enableLookupSyncOnStartup,
        numLookupLoadingThreads,
        coordinatorFetchRetries,
        lookupStartRetries,
        coordinatorRetryDelay
    );
  }

  @Override
  public String toString()
  {
    return "LookupConfig{" +
           "snapshotWorkingDir='" + snapshotWorkingDir + '\'' +
           ", enableLookupSyncOnStartup=" + enableLookupSyncOnStartup +
           ", numLookupLoadingThreads=" + numLookupLoadingThreads +
           ", coordinatorFetchRetries=" + coordinatorFetchRetries +
           ", lookupStartRetries=" + lookupStartRetries +
           ", coordinatorRetryDelay=" + coordinatorRetryDelay +
           '}';
  }
}
