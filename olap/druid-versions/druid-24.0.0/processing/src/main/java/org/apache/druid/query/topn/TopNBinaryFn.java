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

package org.apache.druid.query.topn;

import org.apache.druid.java.util.common.granularity.AllGranularity;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.AggregatorUtil;
import org.apache.druid.query.aggregation.PostAggregator;
import org.apache.druid.query.dimension.DimensionSpec;
import org.apache.druid.utils.CollectionUtils;
import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 */
public class TopNBinaryFn implements BinaryOperator<Result<TopNResultValue>>
{
  private final DimensionSpec dimSpec;
  private final Granularity gran;
  private final String dimension;
  private final TopNMetricSpec topNMetricSpec;
  private final int threshold;
  private final List<AggregatorFactory> aggregations;
  private final List<PostAggregator> postAggregations;
  private final Comparator comparator;

  public TopNBinaryFn(
      final Granularity granularity,
      final DimensionSpec dimSpec,
      final TopNMetricSpec topNMetricSpec,
      final int threshold,
      final List<AggregatorFactory> aggregatorSpecs,
      final List<PostAggregator> postAggregatorSpecs
  )
  {
    this.dimSpec = dimSpec;
    this.gran = granularity;
    this.topNMetricSpec = topNMetricSpec;
    this.threshold = threshold;
    this.aggregations = aggregatorSpecs;

    this.postAggregations = AggregatorUtil.pruneDependentPostAgg(
        postAggregatorSpecs,
        topNMetricSpec.getMetricName(dimSpec)
    );

    this.dimension = dimSpec.getOutputName();
    this.comparator = topNMetricSpec.getComparator(aggregatorSpecs, postAggregatorSpecs);
  }

  @Override
  public Result<TopNResultValue> apply(Result<TopNResultValue> arg1, Result<TopNResultValue> arg2)
  {
    if (arg1 == null) {
      return arg2;
    }
    if (arg2 == null) {
      return arg1;
    }

    Map<Object, DimensionAndMetricValueExtractor> retVals = new LinkedHashMap<>();

    TopNResultValue arg1Vals = arg1.getValue();
    TopNResultValue arg2Vals = arg2.getValue();

    for (DimensionAndMetricValueExtractor arg1Val : arg1Vals) {
      retVals.put(arg1Val.getDimensionValue(dimension), arg1Val);
    }
    for (DimensionAndMetricValueExtractor arg2Val : arg2Vals) {
      final Object dimensionValue = arg2Val.getDimensionValue(dimension);
      DimensionAndMetricValueExtractor arg1Val = retVals.get(dimensionValue);

      if (arg1Val != null) {
        // size of map = aggregator + topNDim + postAgg (If sorting is done on post agg field)
        Map<String, Object> retVal = CollectionUtils.newLinkedHashMapWithExpectedSize(aggregations.size() + 2);

        retVal.put(dimension, dimensionValue);
        for (AggregatorFactory factory : aggregations) {
          final String metricName = factory.getName();
          retVal.put(metricName, factory.combine(arg1Val.getMetric(metricName), arg2Val.getMetric(metricName)));
        }

        for (PostAggregator pf : postAggregations) {
          retVal.put(pf.getName(), pf.compute(retVal));
        }

        retVals.put(dimensionValue, new DimensionAndMetricValueExtractor(retVal));
      } else {
        retVals.put(dimensionValue, arg2Val);
      }
    }

    final DateTime timestamp;
    if (gran instanceof AllGranularity) {
      timestamp = arg1.getTimestamp();
    } else {
      timestamp = gran.bucketStart(arg1.getTimestamp());
    }

    TopNResultBuilder bob = topNMetricSpec.getResultBuilder(
        timestamp,
        dimSpec,
        threshold,
        comparator,
        aggregations,
        postAggregations
    );
    for (DimensionAndMetricValueExtractor extractor : retVals.values()) {
      bob.addEntry(extractor);
    }
    return bob.build();
  }
}
