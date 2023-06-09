/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.tubemq.server.common.zookeeper;

import java.util.concurrent.TimeUnit;

/**
 * Utility for retry counter factory.
 *
 * Copied from <a href="http://hbase.apache.org">Apache HBase Project</a>
 */
public class RetryCounterFactory {

    private final int maxRetries;
    private final int retryIntervalMillis;

    public RetryCounterFactory(int maxRetries, int retryIntervalMillis) {
        this.maxRetries = maxRetries;
        this.retryIntervalMillis = retryIntervalMillis;
    }

    public RetryCounter create() {
        return new RetryCounter(maxRetries, retryIntervalMillis, TimeUnit.MILLISECONDS);
    }
}
