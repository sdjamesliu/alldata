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

package org.apache.druid.server.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class SegmentToMove
{
  private final String fromServer;
  private final String toServer;
  private final String segmentName;

  @JsonCreator
  public SegmentToMove(
      @JsonProperty("from") String fromServer,
      @JsonProperty("to") String toServer,
      @JsonProperty("segmentName") String segmentName
  )
  {
    this.fromServer = fromServer;
    this.toServer = toServer;
    this.segmentName = segmentName;
  }

  public String getFromServer()
  {
    return fromServer;
  }

  public String getToServer()
  {
    return toServer;
  }

  public String getSegmentName()
  {
    return segmentName;
  }
}
