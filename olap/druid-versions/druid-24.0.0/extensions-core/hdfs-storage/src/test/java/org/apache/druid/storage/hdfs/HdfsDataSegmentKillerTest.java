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

package org.apache.druid.storage.hdfs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.UUID;

public class HdfsDataSegmentKillerTest
{
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testKill() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "/tmp";
          }
        }
    );

    FileSystem fs = FileSystem.get(config);

    // Create following segments and then delete them in this order and assert directory deletions
    // /tmp/dataSource/interval1/v1/0/index.zip
    // /tmp/dataSource/interval1/v1/1/index.zip
    // /tmp/dataSource/interval1/v2/0/index.zip
    // /tmp/dataSource/interval2/v1/0/index.zip

    Path dataSourceDir = new Path("/tmp/dataSource");

    Path interval1Dir = new Path(dataSourceDir, "interval1");
    Path version11Dir = new Path(interval1Dir, "v1");
    Path partition011Dir = new Path(version11Dir, "0");
    Path partition111Dir = new Path(version11Dir, "1");

    makePartitionDirWithIndex(fs, partition011Dir);
    makePartitionDirWithIndex(fs, partition111Dir);

    Path version21Dir = new Path(interval1Dir, "v2");
    Path partition021Dir = new Path(version21Dir, "0");

    makePartitionDirWithIndex(fs, partition021Dir);

    Path interval2Dir = new Path(dataSourceDir, "interval2");
    Path version12Dir = new Path(interval2Dir, "v1");
    Path partition012Dir = new Path(version12Dir, "0");

    makePartitionDirWithIndex(fs, partition012Dir);

    killer.kill(getSegmentWithPath(new Path(partition011Dir, "index.zip").toString()));

    Assert.assertFalse(fs.exists(partition011Dir));
    Assert.assertTrue(fs.exists(partition111Dir));
    Assert.assertTrue(fs.exists(partition021Dir));
    Assert.assertTrue(fs.exists(partition012Dir));

    killer.kill(getSegmentWithPath(new Path(partition111Dir, "index.zip").toString()));

    Assert.assertFalse(fs.exists(version11Dir));
    Assert.assertTrue(fs.exists(partition021Dir));
    Assert.assertTrue(fs.exists(partition012Dir));

    killer.kill(getSegmentWithPath(new Path(partition021Dir, "index.zip").toString()));

    Assert.assertFalse(fs.exists(interval1Dir));
    Assert.assertTrue(fs.exists(partition012Dir));

    killer.kill(getSegmentWithPath(new Path(partition012Dir, "index.zip").toString()));

    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.delete(dataSourceDir, false));
  }

  @Test
  public void testKillForSegmentPathWithoutPartitionNumber() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "/tmp";
          }
        }
    );

    FileSystem fs = FileSystem.get(config);
    Path dataSourceDir = new Path("/tmp/dataSourceNew");

    Path interval1Dir = new Path(dataSourceDir, "intervalNew");
    Path version11Dir = new Path(interval1Dir, "v1");

    Assert.assertTrue(fs.mkdirs(version11Dir));
    fs.createNewFile(new Path(version11Dir, StringUtils.format("%s_index.zip", 3)));

    killer.kill(getSegmentWithPath(new Path(version11Dir, "3_index.zip").toString()));

    Assert.assertFalse(fs.exists(version11Dir));
    Assert.assertFalse(fs.exists(interval1Dir));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.exists(new Path("/tmp")));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.delete(dataSourceDir, false));
  }

  @Test
  public void testKillForSegmentWithUniquePath() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "/tmp";
          }
        }
    );

    FileSystem fs = FileSystem.get(config);
    Path dataSourceDir = new Path("/tmp/dataSourceNew");

    Path interval1Dir = new Path(dataSourceDir, "intervalNew");
    Path version11Dir = new Path(interval1Dir, "v1");
    String uuid = UUID.randomUUID().toString().substring(0, 5);

    Assert.assertTrue(fs.mkdirs(version11Dir));
    fs.createNewFile(new Path(version11Dir, StringUtils.format("%s_%s_index.zip", 3, uuid)));

    killer.kill(getSegmentWithPath(new Path(version11Dir, StringUtils.format("%s_%s_index.zip", 3, uuid)).toString()));

    Assert.assertFalse(fs.exists(version11Dir));
    Assert.assertFalse(fs.exists(interval1Dir));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.exists(new Path("/tmp")));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.delete(dataSourceDir, false));
  }

  @Test
  public void testKillNonExistingSegment() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "/tmp";
          }
        }
    );

    // Should do nothing.
    killer.kill(getSegmentWithPath(new Path("/xxx/", "index.zip").toString()));
  }

  @Test
  public void testKillNonZipSegment() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "/tmp";
          }
        }
    );

    expectedException.expect(SegmentLoadingException.class);
    expectedException.expectMessage("Unknown file type");
    killer.kill(getSegmentWithPath(new Path("/xxx/", "index.beep").toString()));
  }

  @Test
  public void testNoStorageDirectory() throws Exception
  {
    Configuration config = new Configuration();
    HdfsDataSegmentKiller killer = new HdfsDataSegmentKiller(
        config,
        new HdfsDataSegmentPusherConfig()
        {
          @Override
          public String getStorageDirectory()
          {
            return "";
          }
        }
    );

    FileSystem fs = FileSystem.get(config);
    Path dataSourceDir = new Path("/tmp/dataSourceNew");

    Path interval1Dir = new Path(dataSourceDir, "intervalNew");
    Path version11Dir = new Path(interval1Dir, "v1");

    Assert.assertTrue(fs.mkdirs(version11Dir));
    fs.createNewFile(new Path(version11Dir, StringUtils.format("%s_index.zip", 3)));

    // 'kill' should work even if storageDirectory is not set.
    killer.kill(getSegmentWithPath(new Path(version11Dir, "3_index.zip").toString()));

    // Verify the segment no longer exists, but that its datasource directory does.
    // Then delete its datasource directory.
    Assert.assertFalse(fs.exists(version11Dir));
    Assert.assertFalse(fs.exists(interval1Dir));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.exists(new Path("/tmp")));
    Assert.assertTrue(fs.exists(dataSourceDir));
    Assert.assertTrue(fs.delete(dataSourceDir, false));

    // killAll should *not* work when storageDirectory is not set.
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Cannot delete all segment files since druid.storage.storageDirectory is not set");
    killer.killAll();
  }

  private void makePartitionDirWithIndex(FileSystem fs, Path path) throws IOException
  {
    Assert.assertTrue(fs.mkdirs(path));
    fs.createNewFile(new Path(path, "index.zip"));
  }

  private DataSegment getSegmentWithPath(String path)
  {
    return new DataSegment(
        "dataSource",
        Intervals.of("2000/3000"),
        "ver",
        ImmutableMap.of(
            "type", "hdfs",
            "path", path
        ),
        ImmutableList.of("product"),
        ImmutableList.of("visited_sum", "unique_hosts"),
        NoneShardSpec.instance(),
        9,
        12334
    );
  }
}
