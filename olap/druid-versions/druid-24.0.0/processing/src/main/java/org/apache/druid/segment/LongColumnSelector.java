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

package org.apache.druid.segment;

import javax.annotation.Nullable;

/**
 * This interface is convenient for implementation of "long-sourcing" {@link ColumnValueSelector}s, it provides default
 * implementations for all {@link ColumnValueSelector}'s methods except {@link #getLong()}.
 *
 * This interface should appear ONLY in "implements" clause or anonymous class creation, but NOT in "user" code, where
 * {@link BaseLongColumnValueSelector} must be used instead.
 */
public interface LongColumnSelector extends ColumnValueSelector<Long>
{
  /**
   * @deprecated This method is marked as deprecated in LongColumnSelector to minimize the probability of accidental
   * calling. "Polymorphism" of LongColumnSelector should be used only when operating on {@link ColumnValueSelector}
   * objects.
   */
  @Deprecated
  @Override
  default float getFloat()
  {
    return (float) getLong();
  }

  /**
   * @deprecated This method is marked as deprecated in LongColumnSelector to minimize the probability of accidental
   * calling. "Polymorphism" of LongColumnSelector should be used only when operating on {@link ColumnValueSelector}
   * objects.
   */
  @Deprecated
  @Override
  default double getDouble()
  {
    return (double) getLong();
  }

  /**
   * @deprecated This method is marked as deprecated in LongColumnSelector to minimize the probability of accidental
   * calling. "Polymorphism" of LongColumnSelector should be used only when operating on {@link ColumnValueSelector}
   * objects.
   */
  @Deprecated
  @Override
  @Nullable
  default Long getObject()
  {
    if (isNull()) {
      return null;
    }
    return getLong();
  }

  /**
   * @deprecated This method is marked as deprecated in LongColumnSelector to minimize the probability of accidental
   * calling. "Polymorphism" of LongColumnSelector should be used only when operating on {@link ColumnValueSelector}
   * objects.
   */
  @Deprecated
  @Override
  default Class<Long> classOfObject()
  {
    return Long.class;
  }
}
