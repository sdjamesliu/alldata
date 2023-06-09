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

package org.apache.kylin.common.persistence;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class JDBCSqlQueryFormatProvider {
    static Map<String, Properties> cache = new HashMap<>();

    public static JDBCSqlQueryFormat createJDBCSqlQueriesFormat(String dialect) {
        String key = String.format(Locale.ROOT, "/metadata-jdbc-%s.properties", dialect.toLowerCase(Locale.ROOT));
        if (cache.containsKey(key)) {
            return new JDBCSqlQueryFormat(cache.get(key));
        } else {
            Properties props = new Properties();
            InputStream input = null;
            try {
                input = props.getClass().getResourceAsStream(key);
                props.load(input);
                if (!props.isEmpty()) {
                    cache.put(key, props);
                }
                return new JDBCSqlQueryFormat(props);
            } catch (Exception e) {
                throw new RuntimeException(String.format(Locale.ROOT, "Can't find properties named %s for metastore", key), e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }

    }
}
