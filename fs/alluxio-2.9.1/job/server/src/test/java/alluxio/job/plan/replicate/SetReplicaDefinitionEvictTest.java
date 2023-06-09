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

package alluxio.job.plan.replicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import alluxio.client.block.BlockStoreClient;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.collections.Pair;
import alluxio.job.JobServerContext;
import alluxio.job.SelectExecutorsContext;
import alluxio.job.util.SerializableVoid;
import alluxio.underfs.UfsManager;
import alluxio.wire.BlockInfo;
import alluxio.wire.BlockLocation;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Set;

/**
 * Tests evict functionality of {@link SetReplicaDefinition}.
 */
public final class SetReplicaDefinitionEvictTest {
  private static final long TEST_BLOCK_ID = 1L;
  private static final WorkerNetAddress ADDRESS_1 =
      new WorkerNetAddress().setHost("host1").setDataPort(10);
  private static final WorkerNetAddress ADDRESS_2 =
      new WorkerNetAddress().setHost("host2").setDataPort(10);
  private static final WorkerNetAddress ADDRESS_3 =
      new WorkerNetAddress().setHost("host3").setDataPort(10);
  private static final WorkerInfo WORKER_INFO_1 = new WorkerInfo().setAddress(ADDRESS_1);
  private static final WorkerInfo WORKER_INFO_2 = new WorkerInfo().setAddress(ADDRESS_2);
  private static final WorkerInfo WORKER_INFO_3 = new WorkerInfo().setAddress(ADDRESS_3);
  private static final Set<Pair<WorkerInfo, SerializableVoid>> EMPTY = Sets.newHashSet();

  private FileSystem mMockFileSystem;
  private FileSystemContext mMockFileSystemContext;
  private BlockStoreClient mMockBlockStore;
  private JobServerContext mJobServerContext;
  private MockedStatic<BlockStoreClient> mMockStaticBlockStore;

  @Before
  public void before() {
    mMockFileSystemContext = mock(FileSystemContext.class);
    mMockFileSystem = mock(FileSystem.class);
    mMockBlockStore = mock(BlockStoreClient.class);
    mJobServerContext = new JobServerContext(mMockFileSystem, mMockFileSystemContext,
        mock(UfsManager.class));
    mMockStaticBlockStore = mockStatic(BlockStoreClient.class);
    mMockStaticBlockStore.when(() -> BlockStoreClient.create(mMockFileSystemContext))
        .thenReturn(mMockBlockStore);
  }

  @After
  public void after() {
    mMockStaticBlockStore.close();
  }

  /**
   * Helper function to select executors.
   *
   * @param blockLocations where the block is stored currently
   * @param replicas how many replicas to evict
   * @param workerInfoList a list of currently available job workers
   * @return the selection result
   */
  private Set<Pair<WorkerInfo, SetReplicaTask>> selectExecutorsTestHelper(
      List<BlockLocation> blockLocations, int replicas, List<WorkerInfo> workerInfoList)
      throws Exception {
    BlockInfo blockInfo = new BlockInfo().setBlockId(TEST_BLOCK_ID);
    blockInfo.setLocations(blockLocations);
    when(mMockBlockStore.getInfo(TEST_BLOCK_ID)).thenReturn(blockInfo);

    SetReplicaConfig config = new SetReplicaConfig("", TEST_BLOCK_ID, replicas);
    SetReplicaDefinition definition = new SetReplicaDefinition();
    return definition.selectExecutors(config, workerInfoList,
        new SelectExecutorsContext(1, mJobServerContext));
  }

  @Test
  public void selectExecutorsNoBlockWorkerHasBlock() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result =
        selectExecutorsTestHelper(Lists.<BlockLocation>newArrayList(), 0,
            Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    // Expect none as no worker has this copy
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  public void selectExecutorsNoJobWorkerHasBlock() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(
        Lists.newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1)), 0,
        Lists.newArrayList(WORKER_INFO_2, WORKER_INFO_3));
    // Expect none as no worker that is available has this copy
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  public void selectExecutorsOnlyOneBlockWorkerHasBlock() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(
        Lists.newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1)), 0,
        Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    Set<Pair<WorkerInfo, SetReplicaTask>> expected = Sets.newHashSet();
    expected.add(new Pair<>(WORKER_INFO_1, new SetReplicaTask(Mode.EVICT)));
    // Expect the only worker 1 having this block
    Assert.assertEquals(expected, result);
  }

  @Test
  public void selectExecutorsAnyOneWorkers() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(Lists
            .newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1),
                new BlockLocation().setWorkerAddress(ADDRESS_2),
                new BlockLocation().setWorkerAddress(ADDRESS_3)), 2,
        Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    // Expect one worker from all workers having this block
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(new SetReplicaTask(Mode.EVICT), result.iterator().next().getSecond());
  }

  @Test
  public void selectExecutorsAllWorkers() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(Lists
            .newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1),
                new BlockLocation().setWorkerAddress(ADDRESS_2),
                new BlockLocation().setWorkerAddress(ADDRESS_3)), 0,
        Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    Set<Pair<WorkerInfo, SetReplicaTask>> expected = Sets.newHashSet();
    expected.add(new Pair<>(WORKER_INFO_1, new SetReplicaTask(Mode.EVICT)));
    expected.add(new Pair<>(WORKER_INFO_2, new SetReplicaTask(Mode.EVICT)));
    expected.add(new Pair<>(WORKER_INFO_3, new SetReplicaTask(Mode.EVICT)));
    // Expect all workers are selected as they all have this block
    Assert.assertEquals(expected, result);
  }

  @Test
  public void selectExecutorsBothWorkers() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(Lists
            .newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1),
                new BlockLocation().setWorkerAddress(ADDRESS_2)), 0,
        Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    Set<Pair<WorkerInfo, SetReplicaTask>> expected = Sets.newHashSet();
    expected.add(new Pair<>(WORKER_INFO_1, new SetReplicaTask(Mode.EVICT)));
    expected.add(new Pair<>(WORKER_INFO_2, new SetReplicaTask(Mode.EVICT)));
    // Expect both workers having this block should be selected
    Assert.assertEquals(expected, result);
  }

  @Test
  public void selectExecutorsTargetEqualNumBlocks() throws Exception {
    Set<Pair<WorkerInfo, SetReplicaTask>> result = selectExecutorsTestHelper(
        Lists.newArrayList(new BlockLocation().setWorkerAddress(ADDRESS_1)), 1,
        Lists.newArrayList(WORKER_INFO_1, WORKER_INFO_2, WORKER_INFO_3));
    Assert.assertEquals(EMPTY, result);
  }
}
