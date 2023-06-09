/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.metrics.lib.impl;

import java.util.Properties;

import org.apache.kylin.metrics.lib.ActiveReservoir;
import org.apache.kylin.metrics.lib.ActiveReservoirReporter;

/**
 * Builder of ActiveReservoirReporter
 */
public abstract class ReporterBuilder {
    protected final ActiveReservoir registry;
    protected final Properties props;
    public static final String LISTENER_FILTER_CLASS = "listener.filter.class";
    public static final String CALLBACK_URL = "callback.url";
    public static final String LISTENER_FILTER_DEFAULT_CLASS = "org.apache.kylin.metrics.lib.impl.callback.CallbackActiveReservoirFilter";

    protected ReporterBuilder(ActiveReservoir activeReservoir) {
        this.registry = activeReservoir;
        this.props = new Properties();
    }

    public ReporterBuilder setConfig(Properties props) {
        if (props != null) {
            this.props.putAll(props);
        }
        return this;
    }

    /**
     * Builds a {@link ActiveReservoirReporter} with the given properties.
     *
     * @return a {@link ActiveReservoirReporter}
     */
    public abstract ActiveReservoirReporter build() throws Exception;
}
