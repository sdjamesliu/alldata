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

import org.apache.druid.guice.annotations.PublicApi;
import org.apache.druid.query.monomorphicprocessing.CalledFromHotLoop;
import org.apache.druid.query.monomorphicprocessing.HotLoopCallee;

import javax.annotation.Nullable;

/**
 * Indexed is a fixed-size, immutable, indexed set of values which allows
 * locating a specific index via an exact match, the semantics of which are defined
 * by the implementation. The indexed is ordered, and may contain duplicate values.
 *
 * @param <T> the type of the value
 */
@PublicApi
public interface Indexed<T> extends Iterable<T>, HotLoopCallee
{

  int size();

  @CalledFromHotLoop
  @Nullable
  T get(int index);

  /**
   * Returns the index of "value" in this Indexed object, or a negative number if the value is not present.
   * The negative number is not guaranteed to be any particular number. Subclasses may tighten this contract
   * (GenericIndexed does this).
   *
   * @param value value to search for
   *
   * @return index of value, or a negative number
   */
  int indexOf(@Nullable T value);

  @FunctionalInterface
  interface IndexedGetter<T>
  {
    @Nullable
    T get(int id);
  }
}
