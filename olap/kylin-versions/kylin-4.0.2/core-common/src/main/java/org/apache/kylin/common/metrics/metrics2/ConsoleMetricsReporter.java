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

package org.apache.kylin.common.metrics.metrics2;

import java.util.concurrent.TimeUnit;

import org.apache.kylin.common.KylinConfig;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

/**
 * A wrapper around Codahale ConsoleReporter to make it a pluggable/configurable Kylin Metrics reporter.
 */
public class ConsoleMetricsReporter implements CodahaleReporter {

    private final ConsoleReporter reporter;

    public ConsoleMetricsReporter(MetricRegistry registry, KylinConfig conf) {

        reporter = ConsoleReporter.forRegistry(registry).convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).build();

    }

    @Override
    public void start() {
        reporter.start(10, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        reporter.close();
    }
}
