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

package org.apache.druid.segment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.druid.segment.data.BitmapSerde;
import org.apache.druid.segment.data.BitmapSerdeFactory;
import org.apache.druid.segment.data.CompressionFactory;
import org.apache.druid.segment.data.CompressionStrategy;
import org.apache.druid.segment.loading.SegmentizerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * IndexSpec defines segment storage format options to be used at indexing time,
 * such as bitmap type, and column compression formats.
 *
 * IndexSpec is specified as part of the TuningConfig for the corresponding index task.
 */
public class IndexSpec
{
  public static final CompressionStrategy DEFAULT_METRIC_COMPRESSION = CompressionStrategy.DEFAULT_COMPRESSION_STRATEGY;
  public static final CompressionStrategy DEFAULT_DIMENSION_COMPRESSION = CompressionStrategy.DEFAULT_COMPRESSION_STRATEGY;
  public static final CompressionFactory.LongEncodingStrategy DEFAULT_LONG_ENCODING = CompressionFactory.DEFAULT_LONG_ENCODING_STRATEGY;

  private static final Set<CompressionStrategy> METRIC_COMPRESSION = Sets.newHashSet(
      Arrays.asList(CompressionStrategy.values())
  );

  private static final Set<CompressionStrategy> DIMENSION_COMPRESSION = Sets.newHashSet(
      Arrays.asList(CompressionStrategy.noNoneValues())
  );

  private static final Set<CompressionFactory.LongEncodingStrategy> LONG_ENCODING_NAMES = Sets.newHashSet(
      Arrays.asList(CompressionFactory.LongEncodingStrategy.values())
  );

  private final BitmapSerdeFactory bitmapSerdeFactory;
  private final CompressionStrategy dimensionCompression;
  private final CompressionStrategy metricCompression;
  private final CompressionFactory.LongEncodingStrategy longEncoding;

  @Nullable
  private final CompressionStrategy jsonCompression;

  @Nullable
  private final SegmentizerFactory segmentLoader;

  /**
   * Creates an IndexSpec with default parameters
   */
  public IndexSpec()
  {
    this(null, null, null, null, null, null);
  }

  @VisibleForTesting
  public IndexSpec(
      @Nullable BitmapSerdeFactory bitmapSerdeFactory,
      @Nullable CompressionStrategy dimensionCompression,
      @Nullable CompressionStrategy metricCompression,
      @Nullable CompressionFactory.LongEncodingStrategy longEncoding
  )
  {
    this(bitmapSerdeFactory, dimensionCompression, metricCompression, longEncoding, null, null);
  }

  @VisibleForTesting
  public IndexSpec(
      @Nullable BitmapSerdeFactory bitmapSerdeFactory,
      @Nullable CompressionStrategy dimensionCompression,
      @Nullable CompressionStrategy metricCompression,
      @Nullable CompressionFactory.LongEncodingStrategy longEncoding,
      @Nullable SegmentizerFactory segmentLoader
  )
  {
    this(bitmapSerdeFactory, dimensionCompression, metricCompression, longEncoding, null, segmentLoader);
  }

  /**
   * Creates an IndexSpec with the given storage format settings.
   *
   *
   * @param bitmapSerdeFactory type of bitmap to use (e.g. roaring or concise), null to use the default.
   *                           Defaults to the bitmap type specified by the (deprecated) "druid.processing.bitmap.type"
   *                           setting, or, if none was set, uses the default defined in {@link BitmapSerde}
   *
   * @param dimensionCompression compression format for dimension columns, null to use the default.
   *                             Defaults to {@link CompressionStrategy#DEFAULT_COMPRESSION_STRATEGY}
   *
   * @param metricCompression compression format for primitive type metric columns, null to use the default.
   *                          Defaults to {@link CompressionStrategy#DEFAULT_COMPRESSION_STRATEGY}
   *
   * @param longEncoding encoding strategy for metric and dimension columns with type long, null to use the default.
   *                     Defaults to {@link CompressionFactory#DEFAULT_LONG_ENCODING_STRATEGY}
   */
  @JsonCreator
  public IndexSpec(
      @JsonProperty("bitmap") @Nullable BitmapSerdeFactory bitmapSerdeFactory,
      @JsonProperty("dimensionCompression") @Nullable CompressionStrategy dimensionCompression,
      @JsonProperty("metricCompression") @Nullable CompressionStrategy metricCompression,
      @JsonProperty("longEncoding") @Nullable CompressionFactory.LongEncodingStrategy longEncoding,
      @JsonProperty("jsonCompression") @Nullable CompressionStrategy jsonCompression,
      @JsonProperty("segmentLoader") @Nullable SegmentizerFactory segmentLoader
  )
  {
    Preconditions.checkArgument(dimensionCompression == null || DIMENSION_COMPRESSION.contains(dimensionCompression),
                                "Unknown compression type[%s]", dimensionCompression);

    Preconditions.checkArgument(metricCompression == null || METRIC_COMPRESSION.contains(metricCompression),
                                "Unknown compression type[%s]", metricCompression);

    Preconditions.checkArgument(longEncoding == null || LONG_ENCODING_NAMES.contains(longEncoding),
                                "Unknown long encoding type[%s]", longEncoding);

    this.bitmapSerdeFactory = bitmapSerdeFactory != null
                              ? bitmapSerdeFactory
                              : new BitmapSerde.DefaultBitmapSerdeFactory();
    this.dimensionCompression = dimensionCompression == null ? DEFAULT_DIMENSION_COMPRESSION : dimensionCompression;
    this.metricCompression = metricCompression == null ? DEFAULT_METRIC_COMPRESSION : metricCompression;
    this.longEncoding = longEncoding == null ? DEFAULT_LONG_ENCODING : longEncoding;
    this.jsonCompression = jsonCompression;
    this.segmentLoader = segmentLoader;
  }

  @JsonProperty("bitmap")
  public BitmapSerdeFactory getBitmapSerdeFactory()
  {
    return bitmapSerdeFactory;
  }

  @JsonProperty
  public CompressionStrategy getDimensionCompression()
  {
    return dimensionCompression;
  }

  @JsonProperty
  public CompressionStrategy getMetricCompression()
  {
    return metricCompression;
  }

  @JsonProperty
  public CompressionFactory.LongEncodingStrategy getLongEncoding()
  {
    return longEncoding;
  }

  @JsonProperty
  @Nullable
  public SegmentizerFactory getSegmentLoader()
  {
    return segmentLoader;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  public CompressionStrategy getJsonCompression()
  {
    return jsonCompression;
  }

  public Map<String, Object> asMap(ObjectMapper objectMapper)
  {
    return objectMapper.convertValue(
        this,
        new TypeReference<Map<String, Object>>() {}
    );
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
    IndexSpec indexSpec = (IndexSpec) o;
    return Objects.equals(bitmapSerdeFactory, indexSpec.bitmapSerdeFactory) &&
           dimensionCompression == indexSpec.dimensionCompression &&
           metricCompression == indexSpec.metricCompression &&
           longEncoding == indexSpec.longEncoding &&
           Objects.equals(jsonCompression, indexSpec.jsonCompression) &&
           Objects.equals(segmentLoader, indexSpec.segmentLoader);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(bitmapSerdeFactory, dimensionCompression, metricCompression, longEncoding, jsonCompression, segmentLoader);
  }

  @Override
  public String toString()
  {
    return "IndexSpec{" +
           "bitmapSerdeFactory=" + bitmapSerdeFactory +
           ", dimensionCompression=" + dimensionCompression +
           ", metricCompression=" + metricCompression +
           ", longEncoding=" + longEncoding +
           ", jsonCompression=" + jsonCompression +
           ", segmentLoader=" + segmentLoader +
           '}';
  }
}
