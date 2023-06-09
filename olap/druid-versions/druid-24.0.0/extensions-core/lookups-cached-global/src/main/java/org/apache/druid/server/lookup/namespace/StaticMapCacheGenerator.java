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

package org.apache.druid.server.lookup.namespace;

import org.apache.druid.query.lookup.namespace.CacheGenerator;
import org.apache.druid.query.lookup.namespace.StaticMapExtractionNamespace;
import org.apache.druid.server.lookup.namespace.cache.CacheHandler;
import org.apache.druid.server.lookup.namespace.cache.CacheScheduler;

import javax.annotation.Nullable;
import java.util.UUID;

public final class StaticMapCacheGenerator implements CacheGenerator<StaticMapExtractionNamespace>
{
  private final String version = UUID.randomUUID().toString();

  @Override
  @Nullable
  public String generateCache(
      final StaticMapExtractionNamespace namespace,
      final CacheScheduler.EntryImpl<StaticMapExtractionNamespace> id,
      final String lastVersion,
      final CacheHandler cache
  )
  {
    if (lastVersion != null) {
      // Throwing AssertionError, because CacheScheduler doesn't suppress Errors and will stop trying to update
      // the cache periodically.
      throw new AssertionError(
          "StaticMapCacheGenerator could only be configured for a namespace which is scheduled "
          + "to be updated once, not periodically. Last version: `" + lastVersion + "`");
    }
    try {
      cache.getCache().putAll(namespace.getMap());
      return version;
    }
    catch (Throwable t) {
      try {
        cache.close();
      }
      catch (Exception e) {
        t.addSuppressed(e);
      }
      throw t;
    }
  }

  String getVersion()
  {
    return version;
  }
}
