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

import com.google.common.collect.ImmutableMap;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.query.lookup.namespace.StaticMapExtractionNamespace;
import org.apache.druid.server.lookup.namespace.cache.CacheHandler;
import org.apache.druid.server.lookup.namespace.cache.CacheScheduler;
import org.apache.druid.server.lookup.namespace.cache.NamespaceExtractionCacheManager;
import org.apache.druid.server.lookup.namespace.cache.OnHeapNamespaceExtractionCacheManager;
import org.apache.druid.server.metrics.NoopServiceEmitter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

public class StaticMapCacheGeneratorTest
{
  private static final Map<String, String> MAP = ImmutableMap.<String, String>builder().put("foo", "bar").build();

  private Lifecycle lifecycle;
  private CacheScheduler scheduler;
  private NamespaceExtractionCacheManager cacheManager;

  @Before
  public void setup() throws Exception
  {
    lifecycle = new Lifecycle();
    lifecycle.start();
    NoopServiceEmitter noopServiceEmitter = new NoopServiceEmitter();
    cacheManager = new OnHeapNamespaceExtractionCacheManager(
        lifecycle,
        noopServiceEmitter,
        new NamespaceExtractionConfig()
    );
    scheduler = new CacheScheduler(
        noopServiceEmitter,
        Collections.emptyMap(),
        cacheManager
    );
  }

  @After
  public void tearDown()
  {
    lifecycle.stop();
  }

  @Test
  public void testSimpleGenerator()
  {
    final StaticMapCacheGenerator factory = new StaticMapCacheGenerator();
    final StaticMapExtractionNamespace namespace = new StaticMapExtractionNamespace(MAP);
    CacheHandler cache = cacheManager.allocateCache();
    final String version = factory.generateCache(namespace, null, null, cache);
    Assert.assertNotNull(version);
    Assert.assertEquals(factory.getVersion(), version);
    Assert.assertEquals(MAP, cache.getCache());

  }

  @Test(expected = AssertionError.class)
  public void testNonNullLastVersionCausesAssertionError()
  {
    final StaticMapCacheGenerator factory = new StaticMapCacheGenerator();
    final StaticMapExtractionNamespace namespace = new StaticMapExtractionNamespace(MAP);
    CacheHandler cache = cacheManager.allocateCache();
    factory.generateCache(namespace, null, factory.getVersion(), cache);
  }
}
