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

package org.apache.druid.cli;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Named;
import org.apache.druid.cli.ServerRunnable.DiscoverySideEffectsProvider;
import org.apache.druid.curator.discovery.ServiceAnnouncer;
import org.apache.druid.discovery.DiscoveryDruidNode;
import org.apache.druid.discovery.DruidNodeAnnouncer;
import org.apache.druid.discovery.DruidService;
import org.apache.druid.discovery.NodeRole;
import org.apache.druid.guice.AbstractDruidServiceModule;
import org.apache.druid.guice.StartupInjectorBuilder;
import org.apache.druid.guice.annotations.Self;
import org.apache.druid.initialization.ServerInjectorBuilder;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.server.DruidNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DiscoverySideEffectsProviderTest
{
  private NodeRole nodeRole;
  @Mock
  private DruidNode druidNode;
  /**
   * This announcer is mocked to fail if it tries to announce a Druid service that is not discoverable.
   */
  @Mock
  private DruidNodeAnnouncer discoverableOnlyAnnouncer;
  @Mock
  private ServiceAnnouncer legacyAnnouncer;
  @Mock
  private Lifecycle lifecycle;
  private List<Lifecycle.Handler> lifecycleHandlers;

  private ServerRunnable.DiscoverySideEffectsProvider target;

  @Before
  public void setUp()
  {
    nodeRole = NodeRole.HISTORICAL;
    lifecycleHandlers = new ArrayList<>();
    Mockito.doAnswer((invocation) -> {
      DiscoveryDruidNode discoveryDruidNode = invocation.getArgument(0);
      boolean isAllServicesDiscoverable =
          discoveryDruidNode.getServices().values().stream().allMatch(DruidService::isDiscoverable);
      Assert.assertTrue(isAllServicesDiscoverable);
      return null;
    }).when(discoverableOnlyAnnouncer).announce(ArgumentMatchers.any(DiscoveryDruidNode.class));
    Mockito
        .doAnswer((invocation) -> lifecycleHandlers.add(invocation.getArgument(0)))
        .when(lifecycle)
        .addHandler(ArgumentMatchers.any(Lifecycle.Handler.class), ArgumentMatchers.eq(Lifecycle.Stage.ANNOUNCEMENTS));
    target = DiscoverySideEffectsProvider.withLegacyAnnouncer();
  }

  @Test
  public void testGetShouldAddAnnouncementsForDiscoverableServices() throws Exception
  {
    createInjector(
        ImmutableList.of(new DiscoverableServiceTestModule(), new UndiscoverableServiceTestModule())
    ).injectMembers(target);
    ServerRunnable.DiscoverySideEffectsProvider.Child child = target.get();
    Assert.assertNotNull(child);
    Assert.assertEquals(1, lifecycleHandlers.size());
    // Start the lifecycle handler. This will make announcements via the announcer
    lifecycleHandlers.get(0).start();
  }

  @Test
  public void testInjectWithEmptyDruidService() throws Exception
  {
    createInjector(ImmutableList.of()).injectMembers(target);
    ServerRunnable.DiscoverySideEffectsProvider.Child child = target.get();
    Assert.assertNotNull(child);
    Assert.assertEquals(1, lifecycleHandlers.size());
    // Start the lifecycle handler. This will make announcements via the announcer
    lifecycleHandlers.get(0).start();
  }

  /**
   * Dummy service which is discoverable.
   */
  private static class DiscoverableDruidService extends DruidService
  {
    @Override
    public String getName()
    {
      return "DiscoverableDruidService";
    }

    @Override
    public boolean isDiscoverable()
    {
      return true;
    }
  }

  /**
   * Dummy service which is not discoverable.
   */
  private static class UndiscoverableDruidService extends DruidService
  {
    @Override
    public String getName()
    {
      return "UnDiscoverableDruidService";
    }

    @Override
    public boolean isDiscoverable()
    {
      return false;
    }
  }

  private static class DiscoverableServiceTestModule extends AbstractDruidServiceModule
  {
    @ProvidesIntoSet
    @Named("historical")
    public Class<? extends DruidService> getDiscoverableDruidService()
    {
      return DiscoverableDruidService.class;
    }

    @Override
    protected NodeRole getNodeRoleKey()
    {
      return NodeRole.HISTORICAL;
    }
  }

  private static class UndiscoverableServiceTestModule extends AbstractDruidServiceModule
  {
    @ProvidesIntoSet
    @Named("historical")
    public Class<? extends DruidService> getUndiscoverableDruidService()
    {
      return UndiscoverableDruidService.class;
    }

    @Override
    protected NodeRole getNodeRoleKey()
    {
      return NodeRole.HISTORICAL;
    }
  }

  private Injector createInjector(List<Module> modules)
  {
    return new StartupInjectorBuilder()
        .add(
            ServerInjectorBuilder.registerNodeRoleModule(ImmutableSet.of(nodeRole)),
            binder -> {
              binder.bind(DruidNodeAnnouncer.class).toInstance(discoverableOnlyAnnouncer);
              binder.bind(DruidNode.class).annotatedWith(Self.class).toInstance(druidNode);
              binder.bind(ServiceAnnouncer.class).toInstance(legacyAnnouncer);
              binder.bind(Lifecycle.class).toInstance(lifecycle);
            }
         )
        .addAll(modules)
        .build();
  }
}
