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

package org.apache.druid.query.groupby.having;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.druid.data.input.Rows;
import org.apache.druid.query.cache.CacheKeyBuilder;
import org.apache.druid.query.extraction.ExtractionFn;
import org.apache.druid.query.extraction.IdentityExtractionFn;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.query.groupby.ResultRow;

import java.util.List;
import java.util.Objects;

public class DimensionSelectorHavingSpec implements HavingSpec
{
  private final String dimension;
  private final String value;
  private final ExtractionFn extractionFn;

  private volatile int columnNumber;

  @JsonCreator
  public DimensionSelectorHavingSpec(
      @JsonProperty("dimension") String dimName,
      @JsonProperty("value") String value,
      @JsonProperty("extractionFn") ExtractionFn extractionFn
  )
  {
    dimension = Preconditions.checkNotNull(dimName, "Must have attribute 'dimension'");
    this.value = value;
    this.extractionFn = extractionFn != null ? extractionFn : IdentityExtractionFn.getInstance();
  }

  @JsonProperty("value")
  public String getValue()
  {
    return value;
  }

  @JsonProperty("dimension")
  public String getDimension()
  {
    return dimension;
  }

  @JsonProperty
  public ExtractionFn getExtractionFn()
  {
    return extractionFn;
  }

  @Override
  public void setQuery(GroupByQuery query)
  {
    columnNumber = query.getResultRowSignature().indexOf(dimension);
  }

  @Override
  public boolean eval(ResultRow row)
  {
    if (columnNumber < 0) {
      return Strings.isNullOrEmpty(value);
    }

    List<String> dimRowValList = Rows.objectToStrings(row.get(columnNumber));
    if (dimRowValList == null || dimRowValList.isEmpty()) {
      return Strings.isNullOrEmpty(value);
    }

    for (String rowVal : dimRowValList) {
      String extracted = getExtractionFn().apply(rowVal);
      if (value != null && value.equals(extracted)) {
        return true;
      }
      if (extracted == null || extracted.isEmpty()) {
        return Strings.isNullOrEmpty(value);
      }
    }

    return false;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final DimensionSelectorHavingSpec that = (DimensionSelectorHavingSpec) o;
    return Objects.equals(dimension, that.dimension) &&
           Objects.equals(value, that.value) &&
           Objects.equals(extractionFn, that.extractionFn);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(dimension, value, extractionFn);
  }

  @Override
  public String toString()
  {
    return "DimensionSelectorHavingSpec{" +
           "dimension='" + dimension + '\'' +
           ", value='" + value + '\'' +
           ", extractionFn=" + extractionFn +
           '}';
  }

  @Override
  public byte[] getCacheKey()
  {
    return new CacheKeyBuilder(HavingSpecUtil.CACHE_TYPE_ID_DIM_SELECTOR)
        .appendString(dimension)
        .appendString(value)
        .appendByteArray(extractionFn == null ? new byte[0] : extractionFn.getCacheKey())
        .build();
  }
}
