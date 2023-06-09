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

package org.apache.druid.query.groupby.epinephelinae.column;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.query.groupby.ResultRow;
import org.apache.druid.query.groupby.epinephelinae.DictionaryBuilding;
import org.apache.druid.query.groupby.epinephelinae.Grouper;
import org.apache.druid.query.ordering.StringComparator;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.segment.ColumnValueSelector;
import org.apache.druid.segment.DimensionDictionary;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.data.ArrayBasedIndexedInts;
import org.apache.druid.segment.data.IndexedInts;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * A String strategy that builds an internal String<->Integer dictionary for
 * DimensionSelectors that return false for nameLookupPossibleInAdvance()
 */
public class DictionaryBuildingStringGroupByColumnSelectorStrategy extends StringGroupByColumnSelectorStrategy
{
  private static final int GROUP_BY_MISSING_VALUE = -1;

  private final List<String> dictionary = DictionaryBuilding.createDictionary();
  private final Object2IntMap<String> reverseDictionary = DictionaryBuilding.createReverseDictionary();

  public DictionaryBuildingStringGroupByColumnSelectorStrategy()
  {
    super(null, null);
  }

  @Override
  public void processValueFromGroupingKey(
      GroupByColumnSelectorPlus selectorPlus,
      ByteBuffer key,
      ResultRow resultRow,
      int keyBufferPosition
  )
  {
    final int id = key.getInt(keyBufferPosition);

    // GROUP_BY_MISSING_VALUE is used to indicate empty rows, which are omitted from the result map.
    if (id != GROUP_BY_MISSING_VALUE) {
      final String value = dictionary.get(id);
      resultRow.set(selectorPlus.getResultRowPosition(), value);
    } else {
      resultRow.set(selectorPlus.getResultRowPosition(), NullHandling.defaultStringValue());
    }
  }

  @Override
  public int initColumnValues(ColumnValueSelector selector, int columnIndex, Object[] valuess)
  {
    final DimensionSelector dimSelector = (DimensionSelector) selector;
    final IndexedInts row = dimSelector.getRow();
    int stateFootprintIncrease = 0;
    ArrayBasedIndexedInts newRow = (ArrayBasedIndexedInts) valuess[columnIndex];
    if (newRow == null) {
      newRow = new ArrayBasedIndexedInts();
      valuess[columnIndex] = newRow;
    }
    int rowSize = row.size();
    newRow.ensureSize(rowSize);
    for (int i = 0; i < rowSize; i++) {
      final String value = dimSelector.lookupName(row.get(i));
      final int dictId = reverseDictionary.getInt(value);
      if (dictId < 0) {
        final int nextId = dictionary.size();
        dictionary.add(value);
        reverseDictionary.put(value, nextId);
        newRow.setValue(i, nextId);
        stateFootprintIncrease +=
            DictionaryBuilding.estimateEntryFootprint((value == null ? 0 : value.length()) * Character.BYTES);
      } else {
        newRow.setValue(i, dictId);
      }
    }
    newRow.setSize(rowSize);
    return stateFootprintIncrease;
  }

  @Override
  public int writeToKeyBuffer(int keyBufferPosition, ColumnValueSelector selector, ByteBuffer keyBuffer)
  {
    final DimensionSelector dimSelector = (DimensionSelector) selector;
    final IndexedInts row = dimSelector.getRow();

    Preconditions.checkState(row.size() < 2, "Not supported for multi-value dimensions");

    if (row.size() == 0) {
      writeToKeyBuffer(keyBufferPosition, GROUP_BY_MISSING_VALUE, keyBuffer);
      return 0;
    }

    final String value = dimSelector.lookupName(row.get(0));
    final int dictId = reverseDictionary.getInt(value);
    if (dictId == DimensionDictionary.ABSENT_VALUE_ID) {
      final int nextId = dictionary.size();
      dictionary.add(value);
      reverseDictionary.put(value, nextId);
      writeToKeyBuffer(keyBufferPosition, nextId, keyBuffer);
      return DictionaryBuilding.estimateEntryFootprint((value == null ? 0 : value.length()) * Character.BYTES);
    } else {
      writeToKeyBuffer(keyBufferPosition, dictId, keyBuffer);
      return 0;
    }
  }

  @Override
  public Grouper.BufferComparator bufferComparator(int keyBufferPosition, @Nullable StringComparator stringComparator)
  {
    final StringComparator realComparator = stringComparator == null ?
                                            StringComparators.LEXICOGRAPHIC :
                                            stringComparator;
    return (lhsBuffer, rhsBuffer, lhsPosition, rhsPosition) -> {
      String lhsStr = dictionary.get(lhsBuffer.getInt(lhsPosition + keyBufferPosition));
      String rhsStr = dictionary.get(rhsBuffer.getInt(rhsPosition + keyBufferPosition));
      return realComparator.compare(lhsStr, rhsStr);
    };
  }

  @Override
  public void reset()
  {
    dictionary.clear();
    reverseDictionary.clear();
  }
}
