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

package org.apache.druid.query.aggregation.distinctcount;

import org.apache.druid.collections.bitmap.MutableBitmap;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.data.IndexedInts;

public class DistinctCountAggregator implements Aggregator
{

  private final DimensionSelector selector;
  private final MutableBitmap mutableBitmap;

  public DistinctCountAggregator(
      DimensionSelector selector,
      MutableBitmap mutableBitmap
  )
  {
    this.selector = selector;
    this.mutableBitmap = mutableBitmap;
  }

  @Override
  public void aggregate()
  {
    IndexedInts row = selector.getRow();
    for (int i = 0, rowSize = row.size(); i < rowSize; i++) {
      int index = row.get(i);
      mutableBitmap.add(index);
    }
  }

  @Override
  public Object get()
  {
    return mutableBitmap.size();
  }

  @Override
  public float getFloat()
  {
    return (float) mutableBitmap.size();
  }

  @Override
  public void close()
  {
    mutableBitmap.clear();
  }

  @Override
  public long getLong()
  {
    return (long) mutableBitmap.size();
  }

  @Override
  public double getDouble()
  {
    return (double) mutableBitmap.size();
  }
}
