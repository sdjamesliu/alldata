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

package org.apache.druid.sql.calcite.expression;

import com.google.common.base.Preconditions;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.dimension.DimensionSpec;
import org.apache.druid.query.dimension.ExtractionDimensionSpec;
import org.apache.druid.query.extraction.ExtractionFn;
import org.apache.druid.segment.column.ColumnType;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents a "simple" extraction of a value from a Druid row, which is defined as a column plus an optional
 * extractionFn. This is useful since identifying simple extractions and treating them specially can allow Druid to
 * perform additional optimizations.
 */
public class SimpleExtraction
{
  private final String column;
  @Nullable
  private final ExtractionFn extractionFn;

  public SimpleExtraction(String column, @Nullable ExtractionFn extractionFn)
  {
    this.column = Preconditions.checkNotNull(column, "column");
    this.extractionFn = extractionFn;
  }

  public static SimpleExtraction of(String column, @Nullable ExtractionFn extractionFn)
  {
    return new SimpleExtraction(column, extractionFn);
  }

  public String getColumn()
  {
    return column;
  }

  @Nullable
  public ExtractionFn getExtractionFn()
  {
    return extractionFn;
  }

  public SimpleExtraction cascade(final ExtractionFn nextExtractionFn)
  {
    return new SimpleExtraction(
        column,
        ExtractionFns.cascade(extractionFn, Preconditions.checkNotNull(nextExtractionFn, "nextExtractionFn"))
    );
  }

  public DimensionSpec toDimensionSpec(
      final String outputName,
      final ColumnType outputType
  )
  {
    Preconditions.checkNotNull(outputType, "outputType");
    return extractionFn == null
           ? new DefaultDimensionSpec(column, outputName, outputType)
           : new ExtractionDimensionSpec(column, outputName, outputType, extractionFn);
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
    SimpleExtraction that = (SimpleExtraction) o;
    return column.equals(that.column) &&
           Objects.equals(extractionFn, that.extractionFn);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(column, extractionFn);
  }

  @Override
  public String toString()
  {
    if (extractionFn != null) {
      return StringUtils.format("%s(%s)", extractionFn, column);
    } else {
      return column;
    }
  }
}
