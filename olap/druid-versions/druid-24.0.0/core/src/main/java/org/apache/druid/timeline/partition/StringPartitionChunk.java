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

package org.apache.druid.timeline.partition;

import org.apache.druid.data.input.StringTuple;

import java.util.Objects;

/**
 */
public class StringPartitionChunk<T> implements PartitionChunk<T>
{
  private final StringTuple start;
  private final StringTuple end;
  private final int chunkNumber;
  private final T object;

  public static <T> StringPartitionChunk<T> makeForSingleDimension(String start, String end, int chunkNumber, T obj)
  {
    return new StringPartitionChunk<>(
        start == null ? null : StringTuple.create(start),
        end == null ? null : StringTuple.create(end),
        chunkNumber,
        obj
    );
  }

  public static <T> StringPartitionChunk<T> make(StringTuple start, StringTuple end, int chunkNumber, T obj)
  {
    return new StringPartitionChunk<>(start, end, chunkNumber, obj);
  }

  private StringPartitionChunk(
      StringTuple start,
      StringTuple end,
      int chunkNumber,
      T object
  )
  {
    this.start = start;
    this.end = end;
    this.chunkNumber = chunkNumber;
    this.object = object;
  }

  @Override
  public T getObject()
  {
    return object;
  }

  @Override
  public boolean abuts(PartitionChunk<T> chunk)
  {
    if (chunk instanceof StringPartitionChunk) {
      StringPartitionChunk<T> stringChunk = (StringPartitionChunk<T>) chunk;

      return !stringChunk.isStart() && Objects.equals(stringChunk.start, end);
    }

    return false;
  }

  @Override
  public boolean isStart()
  {
    return start == null;
  }

  @Override

  public boolean isEnd()
  {
    return end == null;
  }

  @Override
  public int getChunkNumber()
  {
    return chunkNumber;
  }

  @Override
  public int compareTo(PartitionChunk<T> chunk)
  {
    if (chunk instanceof StringPartitionChunk) {
      StringPartitionChunk<T> stringChunk = (StringPartitionChunk<T>) chunk;

      return Integer.compare(chunkNumber, stringChunk.chunkNumber);
    }
    throw new IllegalArgumentException("Cannot compare against something that is not a StringPartitionChunk.");
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return compareTo((StringPartitionChunk<T>) o) == 0;
  }

  @Override
  public int hashCode()
  {
    int result = Objects.hashCode(start);
    result = 31 * result + Objects.hashCode(end);
    result = 31 * result + (object != null ? object.hashCode() : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return "StringPartitionChunk{" +
           "start='" + start + '\'' +
           ", end='" + end + '\'' +
           ", chunkNumber=" + chunkNumber +
           ", object=" + object +
           '}';
  }
}
