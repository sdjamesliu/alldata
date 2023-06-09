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

package org.apache.druid.server.emitter;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.druid.java.util.emitter.core.Emitter;
import org.apache.druid.java.util.emitter.core.HttpPostEmitter;
import org.apache.druid.java.util.emitter.core.ParametrizedUriEmitter;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.java.util.metrics.AbstractMonitor;
import org.apache.druid.java.util.metrics.FeedDefiningMonitor;
import org.apache.druid.java.util.metrics.HttpPostEmitterMonitor;
import org.apache.druid.java.util.metrics.ParametrizedUriEmitterMonitor;

/**
 * Able to monitor {@link HttpPostEmitter} or {@link ParametrizedUriEmitter}, which is based on the former.
 */
public class HttpEmittingMonitor extends AbstractMonitor
{
  private AbstractMonitor delegate;

  @Inject
  public HttpEmittingMonitor(Emitter emitter)
  {
    if (emitter instanceof HttpPostEmitter) {
      delegate = new HttpPostEmitterMonitor(
          FeedDefiningMonitor.DEFAULT_METRICS_FEED,
          (HttpPostEmitter) emitter,
          ImmutableMap.of()
      );
    } else if (emitter instanceof ParametrizedUriEmitter) {
      delegate = new ParametrizedUriEmitterMonitor(
          FeedDefiningMonitor.DEFAULT_METRICS_FEED,
          (ParametrizedUriEmitter) emitter
      );
    } else {
      throw new IllegalStateException(
          "Unable to use HttpEmittingMonitor with emitter other than HttpPostEmitter or ParametrizedUriEmitter, " +
          emitter.getClass() + " is used"
      );
    }
  }

  @Override
  public boolean doMonitor(ServiceEmitter serviceEmitter)
  {
    return delegate.doMonitor(serviceEmitter);
  }
}
