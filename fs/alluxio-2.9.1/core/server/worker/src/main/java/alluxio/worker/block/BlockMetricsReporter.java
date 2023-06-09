/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.worker.block;

import static alluxio.worker.block.BlockMetadataManager.WORKER_STORAGE_TIER_ASSOC;

import alluxio.metrics.MetricKey;
import alluxio.metrics.MetricsSystem;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This class listens on block events and increases the metrics counters.
 */
@ThreadSafe
public final class BlockMetricsReporter extends AbstractBlockStoreEventListener {
  private static final Counter BLOCKS_ACCESSED
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_ACCESSED.getName());
  private static final Counter BLOCKS_PROMOTED
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_PROMOTED.getName());
  private static final Counter BLOCKS_DELETED
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_DELETED.getName());
  private static final Counter BLOCKS_EVICTED
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_EVICTED.getName());
  private static final Counter BLOCKS_CANCELLED
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_CANCELLED.getName());
  private static final Counter BLOCKS_LOST
      = MetricsSystem.counter(MetricKey.WORKER_BLOCKS_LOST.getName());

  private static final Meter BLOCKS_EVICTION_RATE =
      MetricsSystem.meterWithTags(MetricKey.WORKER_BLOCKS_EVICTION_RATE.getName(),
        MetricKey.WORKER_BLOCKS_EVICTION_RATE.isClusterAggregated());

  @Override
  public void onAccessBlock(long blockId) {
    BLOCKS_ACCESSED.inc();
  }

  @Override
  public void onMoveBlockByClient(long blockId, BlockStoreLocation oldLocation,
      BlockStoreLocation newLocation) {
    int oldTierOrdinal = WORKER_STORAGE_TIER_ASSOC.getOrdinal(oldLocation.tierAlias());
    int newTierOrdinal = WORKER_STORAGE_TIER_ASSOC.getOrdinal(newLocation.tierAlias());
    if (newTierOrdinal == 0 && oldTierOrdinal != newTierOrdinal) {
      BLOCKS_PROMOTED.inc();
    }
  }

  @Override
  public void onRemoveBlockByClient(long blockId) {
    BLOCKS_DELETED.inc();
  }

  @Override
  public void onMoveBlockByWorker(long blockId, BlockStoreLocation oldLocation,
      BlockStoreLocation newLocation) {
    int oldTierOrdinal = WORKER_STORAGE_TIER_ASSOC.getOrdinal(oldLocation.tierAlias());
    int newTierOrdinal = WORKER_STORAGE_TIER_ASSOC.getOrdinal(newLocation.tierAlias());
    if (newTierOrdinal == 0 && oldTierOrdinal != newTierOrdinal) {
      BLOCKS_PROMOTED.inc();
    }
  }

  @Override
  public void onRemoveBlockByWorker(long blockId) {
    BLOCKS_EVICTED.inc();
    BLOCKS_EVICTION_RATE.mark();
  }

  @Override
  public void onAbortBlock(long blockId) {
    BLOCKS_CANCELLED.inc();
  }

  @Override
  public void onBlockLost(long blockId) {
    BLOCKS_LOST.inc();
  }
}
