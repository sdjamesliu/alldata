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

package org.apache.druid.segment.data;

import com.google.common.base.Supplier;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class EntireLayoutColumnarFloatsSupplier implements Supplier<ColumnarFloats>
{
  private final int totalSize;
  private FloatBuffer buffer;

  public EntireLayoutColumnarFloatsSupplier(int totalSize, ByteBuffer fromBuffer, ByteOrder order)
  {
    this.totalSize = totalSize;
    this.buffer = fromBuffer.asReadOnlyBuffer().order(order).asFloatBuffer();
  }

  @Override
  public ColumnarFloats get()
  {
    return new EntireLayoutColumnarFloats();
  }

  private class EntireLayoutColumnarFloats implements ColumnarFloats
  {

    @Override
    public int size()
    {
      return totalSize;
    }

    @Override
    public float get(int index)
    {
      return buffer.get(buffer.position() + index);
    }

    @Override
    public String toString()
    {
      return "EntireLayoutColumnarFloats{" +
             ", totalSize=" + totalSize +
             '}';
    }

    @Override
    public void close()
    {
    }
  }
}
