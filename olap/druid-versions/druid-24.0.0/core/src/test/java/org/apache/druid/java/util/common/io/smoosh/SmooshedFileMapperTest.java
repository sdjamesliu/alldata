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

package org.apache.druid.java.util.common.io.smoosh;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import junit.framework.Assert;
import org.apache.druid.java.util.common.BufferUtils;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 */
public class SmooshedFileMapperTest
{
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testSanity() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      for (int i = 0; i < 20; ++i) {
        File tmpFile = folder.newFile(StringUtils.format("smoosh-%s.bin", i));
        Files.write(Ints.toByteArray(i), tmpFile);
        smoosher.add(StringUtils.format("%d", i), tmpFile);
      }
    }
    validateOutput(baseDir);
  }

  @Test
  public void testWhenFirstWriterClosedInTheMiddle() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      final SmooshedWriter writer = smoosher.addWithSmooshedWriter(StringUtils.format("%d", 19), 4);

      for (int i = 0; i < 19; ++i) {
        File tmpFile = File.createTempFile(StringUtils.format("smoosh-%s", i), ".bin");
        Files.write(Ints.toByteArray(i), tmpFile);
        smoosher.add(StringUtils.format("%d", i), tmpFile);
        if (i == 10) {
          writer.write(ByteBuffer.wrap(Ints.toByteArray(19)));
          writer.close();
        }
        tmpFile.delete();
      }
    }
    validateOutput(baseDir);
  }

  @Test(expected = ISE.class)
  public void testExceptionForUnClosedFiles() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      for (int i = 0; i < 19; ++i) {
        final SmooshedWriter writer = smoosher.addWithSmooshedWriter(StringUtils.format("%d", i), 4);
        writer.write(ByteBuffer.wrap(Ints.toByteArray(i)));
      }
    }
  }

  @Test
  public void testWhenFirstWriterClosedAtTheEnd() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      final SmooshedWriter writer = smoosher.addWithSmooshedWriter(StringUtils.format("%d", 19), 4);
      writer.write(ByteBuffer.wrap(Ints.toByteArray(19)));

      for (int i = 0; i < 19; ++i) {
        File tmpFile = File.createTempFile(StringUtils.format("smoosh-%s", i), ".bin");
        Files.write(Ints.toByteArray(i), tmpFile);
        smoosher.add(StringUtils.format("%d", i), tmpFile);
        tmpFile.delete();
      }
      writer.close();
    }
    validateOutput(baseDir);
  }

  @Test
  public void testWhenWithPathyLookingFileNames() throws Exception
  {
    String prefix = "foo/bar/";
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      final SmooshedWriter writer = smoosher.addWithSmooshedWriter(StringUtils.format("%s%d", prefix, 19), 4);
      writer.write(ByteBuffer.wrap(Ints.toByteArray(19)));

      for (int i = 0; i < 19; ++i) {
        File tmpFile = File.createTempFile(StringUtils.format("smoosh-%s", i), ".bin");
        Files.write(Ints.toByteArray(i), tmpFile);
        smoosher.add(StringUtils.format("%s%d", prefix, i), tmpFile);
        tmpFile.delete();
      }
      writer.close();
    }
    validateOutput(baseDir, prefix);
  }

  @Test
  public void testBehaviorWhenReportedSizesLargeAndExceptionIgnored() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      for (int i = 0; i < 20; ++i) {
        final SmooshedWriter writer = smoosher.addWithSmooshedWriter(StringUtils.format("%d", i), 7);
        writer.write(ByteBuffer.wrap(Ints.toByteArray(i)));
        try {
          writer.close();
          Assert.fail("IOException expected");
        }
        catch (IOException ignored) {
          // expected
        }
      }
    }

    File[] files = baseDir.listFiles();
    Assert.assertNotNull(files);
    Arrays.sort(files);

    Assert.assertEquals(6, files.length); // 4 smoosh files and 1 meta file
    for (int i = 0; i < 4; ++i) {
      Assert.assertEquals(FileSmoosher.makeChunkFile(baseDir, i), files[i]);
    }
    Assert.assertEquals(FileSmoosher.metaFile(baseDir), files[files.length - 1]);

    try (SmooshedFileMapper mapper = SmooshedFileMapper.load(baseDir)) {
      for (int i = 0; i < 20; ++i) {
        ByteBuffer buf = mapper.mapFile(StringUtils.format("%d", i));
        Assert.assertEquals(0, buf.position());
        Assert.assertEquals(4, buf.remaining());
        Assert.assertEquals(4, buf.capacity());
        Assert.assertEquals(i, buf.getInt());
      }
    }
  }

  @Test
  public void testBehaviorWhenReportedSizesSmall() throws Exception
  {
    File baseDir = folder.newFolder("base");

    try (FileSmoosher smoosher = new FileSmoosher(baseDir, 21)) {
      boolean exceptionThrown = false;
      try (final SmooshedWriter writer = smoosher.addWithSmooshedWriter("1", 2)) {
        writer.write(ByteBuffer.wrap(Ints.toByteArray(1)));
      }
      catch (ISE e) {
        Assert.assertTrue(e.getMessage().contains("Liar!!!"));
        exceptionThrown = true;
      }

      Assert.assertTrue(exceptionThrown);
      File[] files = baseDir.listFiles();
      Assert.assertNotNull(files);
      Assert.assertEquals(1, files.length);
    }
  }

  @Test
  public void testDeterministicFileUnmapping() throws IOException
  {
    File baseDir = folder.newFolder("base");

    long totalMemoryUsedBeforeAddingFile = BufferUtils.totalMemoryUsedByDirectAndMappedBuffers();
    try (FileSmoosher smoosher = new FileSmoosher(baseDir)) {
      File dataFile = folder.newFile("data.bin");
      try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw")) {
        raf.setLength(1 << 20); // 1 MiB
      }
      smoosher.add(dataFile);
    }
    long totalMemoryUsedAfterAddingFile = BufferUtils.totalMemoryUsedByDirectAndMappedBuffers();
    // Assert no hanging file mappings left by either smoosher or smoosher.add(file)
    Assert.assertEquals(totalMemoryUsedBeforeAddingFile, totalMemoryUsedAfterAddingFile);
  }

  private void validateOutput(File baseDir) throws IOException
  {
    validateOutput(baseDir, "");
  }

  private void validateOutput(File baseDir, String prefix) throws IOException
  {
    File[] files = baseDir.listFiles();
    Arrays.sort(files);

    Assert.assertEquals(5, files.length); // 4 smooshed files and 1 meta file
    for (int i = 0; i < 4; ++i) {
      Assert.assertEquals(FileSmoosher.makeChunkFile(baseDir, i), files[i]);
    }
    Assert.assertEquals(FileSmoosher.metaFile(baseDir), files[files.length - 1]);

    try (SmooshedFileMapper mapper = SmooshedFileMapper.load(baseDir)) {
      for (int i = 0; i < 20; ++i) {
        ByteBuffer buf = mapper.mapFile(StringUtils.format("%s%d", prefix, i));
        Assert.assertEquals(0, buf.position());
        Assert.assertEquals(4, buf.remaining());
        Assert.assertEquals(4, buf.capacity());
        Assert.assertEquals(i, buf.getInt());
      }
    }
  }
}
