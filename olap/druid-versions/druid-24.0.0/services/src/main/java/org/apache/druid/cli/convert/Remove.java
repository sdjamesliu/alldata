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

package org.apache.druid.cli.convert;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Properties;

public class Remove implements PropertyConverter
{
  private final String propertyToRemove;

  public Remove(String propertyToRemove)
  {
    this.propertyToRemove = propertyToRemove;
  }

  @Override
  public boolean canHandle(String property)
  {
    return this.propertyToRemove.equals(property);
  }

  @Override
  public Map<String, String> convert(Properties properties)
  {
    return ImmutableMap.of();
  }
}
