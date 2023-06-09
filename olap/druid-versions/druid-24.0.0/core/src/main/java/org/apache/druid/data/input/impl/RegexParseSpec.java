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

package org.apache.druid.data.input.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import org.apache.druid.java.util.common.parsers.Parser;
import org.apache.druid.java.util.common.parsers.RegexParser;

import java.util.List;

/**
 */
public class RegexParseSpec extends ParseSpec
{
  private final String listDelimiter;
  private final List<String> columns;
  private final String pattern;

  @JsonCreator
  public RegexParseSpec(
      @JsonProperty("timestampSpec") TimestampSpec timestampSpec,
      @JsonProperty("dimensionsSpec") DimensionsSpec dimensionsSpec,
      @JsonProperty("listDelimiter") String listDelimiter,
      @JsonProperty("columns") List<String> columns,
      @JsonProperty("pattern") String pattern
  )
  {
    super(timestampSpec, dimensionsSpec);

    this.listDelimiter = listDelimiter;
    this.columns = columns;
    this.pattern = pattern;
  }

  @JsonProperty
  public String getListDelimiter()
  {
    return listDelimiter;
  }

  @JsonProperty("pattern")
  public String getPattern()
  {
    return pattern;
  }

  @JsonProperty
  public List<String> getColumns()
  {
    return columns;
  }

  @Override
  public Parser<String, Object> makeParser()
  {
    if (columns == null) {
      return new RegexParser(pattern, Optional.fromNullable(listDelimiter));
    }
    return new RegexParser(pattern, Optional.fromNullable(listDelimiter), columns);
  }

  @Override
  public ParseSpec withTimestampSpec(TimestampSpec spec)
  {
    return new RegexParseSpec(spec, getDimensionsSpec(), listDelimiter, columns, pattern);
  }

  @Override
  public ParseSpec withDimensionsSpec(DimensionsSpec spec)
  {
    return new RegexParseSpec(getTimestampSpec(), spec, listDelimiter, columns, pattern);
  }

}
