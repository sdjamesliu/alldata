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
package org.apache.kylin.common.metrics.common;

/**
 * Metrics Scope to represent duration of an event.
 * <p>
 * Implementation can capture information like the average duration of open scopes,
 * number of open scopes, number of completed scopes.
 * <p>
 * Scopes are created via the Metrics framework (see Metrics#createScope or Metrics$createStoredScope)
 * <p>
 * Scope may be stored by the Metrics framework via 'storedScope' concept for further reference.
 * <p>
 * In either case, it is the caller's responsibility to end the scope via the Metrics framework (see Metrics#endScope)
 */
public interface MetricsScope {
}
