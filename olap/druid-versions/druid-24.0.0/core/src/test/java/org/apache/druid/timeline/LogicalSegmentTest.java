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

package org.apache.druid.timeline;

import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class LogicalSegmentTest
{

  @Test
  public void getStatusReady()
  {
    LogicalSegment logicalSegment = new LogicalSegment()
    {
      @Override
      public Interval getInterval()
      {
        return null;
      }

      @Override
      public Interval getTrueInterval()
      {
        return null;
      }
    };
    Assert.assertEquals(logicalSegment.getStatus(), LogicalSegment.Status.READY);
  }

  @Test
  public void getStatusEmpty()
  {
    LogicalSegment emptyLogicalSegment = new LogicalSegment()
    {
      @Override
      public Interval getInterval()
      {
        return null;
      }

      @Override
      public Interval getTrueInterval()
      {
        return null;
      }
      @Override
      public Status getStatus()
      {
        return Status.EMPTY;
      }
    };
    Assert.assertEquals(emptyLogicalSegment.getStatus(), LogicalSegment.Status.EMPTY);
  }
}

