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

package org.apache.druid.query.groupby.epinephelinae;

import com.google.common.util.concurrent.MoreExecutors;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.common.parsers.CloseableIterator;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.groupby.epinephelinae.ConcurrentGrouperTest.TestKeySerdeFactory;
import org.apache.druid.query.groupby.epinephelinae.ConcurrentGrouperTest.TestResourceHolder;
import org.apache.druid.query.groupby.epinephelinae.Grouper.Entry;
import org.apache.druid.query.groupby.epinephelinae.Grouper.KeySerdeFactory;
import org.apache.druid.testing.InitializedNullHandlingTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ParallelCombinerTest extends InitializedNullHandlingTest
{
  private static final int THREAD_NUM = 8;
  private static final ExecutorService SERVICE = Execs.multiThreaded(THREAD_NUM, "parallel-combiner-test-%d");
  private static final TestResourceHolder TEST_RESOURCE_HOLDER = new TestResourceHolder(512);
  private static final KeySerdeFactory<ConcurrentGrouperTest.LongKey> KEY_SERDE_FACTORY = new TestKeySerdeFactory();

  private static final class TestIterator implements CloseableIterator<Entry<ConcurrentGrouperTest.LongKey>>
  {
    private final Iterator<Entry<ConcurrentGrouperTest.LongKey>> innerIterator;
    private boolean closed;

    TestIterator(Iterator<Entry<ConcurrentGrouperTest.LongKey>> innerIterator)
    {
      this.innerIterator = innerIterator;
    }

    @Override
    public boolean hasNext()
    {
      return innerIterator.hasNext();
    }

    @Override
    public Entry<ConcurrentGrouperTest.LongKey> next()
    {
      return innerIterator.next();
    }

    public boolean isClosed()
    {
      return closed;
    }

    @Override
    public void close()
    {
      if (!closed) {
        closed = true;
      }
    }
  }

  @AfterClass
  public static void teardown()
  {
    SERVICE.shutdownNow();
  }

  @Test
  public void testCombine() throws IOException
  {
    final ParallelCombiner<ConcurrentGrouperTest.LongKey> combiner = new ParallelCombiner<>(
        TEST_RESOURCE_HOLDER,
        new AggregatorFactory[]{new CountAggregatorFactory("cnt").getCombiningFactory()},
        KEY_SERDE_FACTORY,
        MoreExecutors.listeningDecorator(SERVICE),
        false,
        THREAD_NUM,
        0, // default priority
        0, // default timeout
        4
    );

    final int numRows = 1000;
    final List<Entry<ConcurrentGrouperTest.LongKey>> baseIterator = new ArrayList<>(numRows);
    for (long i = 0; i < numRows; i++) {
      baseIterator.add(new ReusableEntry<>(new ConcurrentGrouperTest.LongKey(i), new Object[]{i * 10}));
    }

    final int leafNum = 8;
    final List<TestIterator> iterators = new ArrayList<>(leafNum);
    for (int i = 0; i < leafNum; i++) {
      iterators.add(new TestIterator(baseIterator.iterator()));
    }

    try (final CloseableIterator<Entry<ConcurrentGrouperTest.LongKey>> iterator =
             combiner.combine(iterators, new ArrayList<>())) {
      long expectedKey = 0;
      while (iterator.hasNext()) {
        final Entry<ConcurrentGrouperTest.LongKey> expectedEntry = new ReusableEntry<>(
            new ConcurrentGrouperTest.LongKey(expectedKey),
            new Object[]{expectedKey * leafNum * 10}
        );

        final Entry<ConcurrentGrouperTest.LongKey> actualEntry = iterator.next();

        GrouperTestUtil.assertEntriesEqual(expectedEntry, actualEntry);
        expectedKey++;
      }
    }

    iterators.forEach(it -> Assert.assertTrue(it.isClosed()));
  }
}
