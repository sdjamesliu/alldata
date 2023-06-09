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

package org.apache.druid.frame.processor.test;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.apache.druid.frame.Frame;
import org.apache.druid.frame.channel.ReadableFrameChannel;
import org.apache.druid.frame.channel.WritableFrameChannel;
import org.apache.druid.frame.processor.FrameProcessor;
import org.apache.druid.frame.processor.FrameProcessors;
import org.apache.druid.frame.processor.ReturnOrAwait;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Processor used by {@link org.apache.druid.frame.processor.FrameProcessorExecutorTest}.
 *
 * Reads from any number of input channels, and writes each input frame to all output channels.
 * (Hence the "blaster" name.)
 */
public class SuperBlasterFrameProcessor implements FrameProcessor<Long>
{
  public enum AwaitStyle
  {
    NONE,
    ALL,
    ANY,
    SINGLE_ALL,
    SINGLE_ANY
  }

  private final List<ReadableFrameChannel> inChannels;
  private final List<WritableFrameChannel> outChannels;
  private final AwaitStyle awaitStyle;

  // Used to pick random channels to wait for.
  private final Random rand;

  private long rowsRead = 0L;

  public SuperBlasterFrameProcessor(
      final List<ReadableFrameChannel> inChannels,
      final List<WritableFrameChannel> outChannels,
      final AwaitStyle awaitStyle
  )
  {
    this.inChannels = inChannels;
    this.outChannels = outChannels;
    this.awaitStyle = awaitStyle;
    this.rand = new Random(0);
  }

  @Override
  public List<ReadableFrameChannel> inputChannels()
  {
    return inChannels;
  }

  @Override
  public List<WritableFrameChannel> outputChannels()
  {
    return outChannels;
  }

  @Override
  public ReturnOrAwait<Long> runIncrementally(final IntSet readableInputs) throws IOException
  {
    if (readableInputs.size() == inChannels.size() && inChannels.stream().allMatch(ReadableFrameChannel::isFinished)) {
      return ReturnOrAwait.returnObject(rowsRead);
    }

    for (int channelNumber : readableInputs) {
      final ReadableFrameChannel inChannel = inChannels.get(channelNumber);

      if (!inChannel.isFinished()) {
        final Frame frame = inChannel.read();
        rowsRead += frame.numRows();

        for (WritableFrameChannel outChannel : outChannels) {
          outChannel.write(frame);
        }

        // Only permitted to write one frame to each output channel.
        break;
      }
    }

    switch (awaitStyle) {
      case ALL:
        return ReturnOrAwait.awaitAll(inChannels.size());
      case ANY:
        return ReturnOrAwait.awaitAny(IntSets.fromTo(0, inChannels.size()));
      case SINGLE_ALL:
        return ReturnOrAwait.awaitAll(IntSets.singleton(rand.nextInt(inChannels.size())));
      case SINGLE_ANY:
        return ReturnOrAwait.awaitAny(IntSets.singleton(rand.nextInt(inChannels.size())));
      case NONE:
        return ReturnOrAwait.runAgain();
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void cleanup() throws IOException
  {
    FrameProcessors.closeAll(inChannels, outChannels);
  }
}
