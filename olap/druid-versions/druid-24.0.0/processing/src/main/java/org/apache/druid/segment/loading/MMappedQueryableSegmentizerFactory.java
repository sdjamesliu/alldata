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

package org.apache.druid.segment.loading;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.google.common.base.Preconditions;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.QueryableIndexSegment;
import org.apache.druid.segment.Segment;
import org.apache.druid.segment.SegmentLazyLoadFailCallback;
import org.apache.druid.timeline.DataSegment;

import java.io.File;
import java.io.IOException;

/**
 */
public class MMappedQueryableSegmentizerFactory implements SegmentizerFactory
{

  private final IndexIO indexIO;

  public MMappedQueryableSegmentizerFactory(@JacksonInject IndexIO indexIO)
  {
    this.indexIO = Preconditions.checkNotNull(indexIO, "Null IndexIO");
  }

  @Override
  public Segment factorize(DataSegment dataSegment, File parentDir, boolean lazy, SegmentLazyLoadFailCallback loadFailed) throws SegmentLoadingException
  {
    try {
      return new QueryableIndexSegment(indexIO.loadIndex(parentDir, lazy, loadFailed), dataSegment.getId());
    }
    catch (IOException e) {
      throw new SegmentLoadingException(e, "%s", e.getMessage());
    }
  }
}
