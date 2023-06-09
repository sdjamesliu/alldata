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

package org.apache.druid.query.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.druid.query.metadata.metadata.SegmentMetadataQuery;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.util.EnumSet;

public class SegmentMetadataQueryConfig
{
  private static final String DEFAULT_PERIOD_STRING = "P1W";
  private static final PeriodFormatter ISO_FORMATTER = ISOPeriodFormat.standard();
  private static final EnumSet<SegmentMetadataQuery.AnalysisType> DEFAULT_ANALYSIS_TYPES = EnumSet.of(
      SegmentMetadataQuery.AnalysisType.CARDINALITY,
      SegmentMetadataQuery.AnalysisType.INTERVAL,
      SegmentMetadataQuery.AnalysisType.MINMAX
  );

  @JsonProperty
  private Period defaultHistory = ISO_FORMATTER.parsePeriod(DEFAULT_PERIOD_STRING);

  @JsonProperty
  private EnumSet<SegmentMetadataQuery.AnalysisType> defaultAnalysisTypes = DEFAULT_ANALYSIS_TYPES;

  public SegmentMetadataQueryConfig(String period)
  {
    defaultHistory = ISO_FORMATTER.parsePeriod(period);
  }

  public SegmentMetadataQueryConfig()
  {
  }

  public Period getDefaultHistory()
  {
    return defaultHistory;
  }

  public EnumSet<SegmentMetadataQuery.AnalysisType> getDefaultAnalysisTypes()
  {
    return defaultAnalysisTypes;
  }

  public void setDefaultAnalysisTypes(EnumSet<SegmentMetadataQuery.AnalysisType> defaultAnalysisTypes)
  {
    this.defaultAnalysisTypes = defaultAnalysisTypes;
  }
}
