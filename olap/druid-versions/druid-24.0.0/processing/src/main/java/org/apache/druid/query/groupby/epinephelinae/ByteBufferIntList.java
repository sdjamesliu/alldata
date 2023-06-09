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

import org.apache.druid.java.util.common.IAE;
import org.apache.druid.java.util.common.StringUtils;

import java.nio.ByteBuffer;

public class ByteBufferIntList
{
  private final ByteBuffer buffer;
  private final int maxElements;
  private int numElements;

  public ByteBufferIntList(
      ByteBuffer buffer,
      int maxElements
  )
  {
    this.buffer = buffer;
    this.maxElements = maxElements;
    this.numElements = 0;

    if (buffer.capacity() < (maxElements * Integer.BYTES)) {
      throw new IAE(
          "buffer for list is too small, was [%s] bytes, but need [%s] bytes.",
          buffer.capacity(),
          maxElements * Integer.BYTES
      );
    }
  }

  public void add(int val)
  {
    if (numElements == maxElements) {
      throw new IndexOutOfBoundsException(StringUtils.format("List is full with %d elements.", maxElements));
    }
    buffer.putInt(numElements * Integer.BYTES, val);
    numElements++;
  }

  public void set(int index, int val)
  {
    buffer.putInt(index * Integer.BYTES, val);
  }

  public int get(int index)
  {
    return buffer.getInt(index * Integer.BYTES);
  }

  public void reset()
  {
    numElements = 0;
  }
}
