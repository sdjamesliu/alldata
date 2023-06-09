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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.kylin.common.KylinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * A metrics reporter for CodahaleMetrics that dumps metrics periodically into a file in JSON format.
 */

public class JsonFileMetricsReporter implements CodahaleReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileMetricsReporter.class);
    private final MetricRegistry metricRegistry;
    private final ObjectWriter jsonWriter;
    private final ScheduledExecutorService executorService;
    private final KylinConfig conf;
    private final long frequency;
    private final String pathString;
    private final Path path;

    public JsonFileMetricsReporter(MetricRegistry registry, KylinConfig conf) {
        this.metricRegistry = registry;
        this.jsonWriter = new ObjectMapper()
                .registerModule(new MetricsModule(TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, false))
                .writerWithDefaultPrettyPrinter();
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.conf = conf;

        frequency = KylinConfig.getInstanceFromEnv().getMetricsReporterFrequency();
        pathString = KylinConfig.getInstanceFromEnv().getMetricsFileLocation();
        path = new Path(pathString);
    }

    @Override
    public void start() {

        final Path tmpPath = new Path(pathString + ".tmp");
        URI tmpPathURI = tmpPath.toUri();
        final FileSystem fs;
        try {
            if (tmpPathURI.getScheme() == null && tmpPathURI.getAuthority() == null) {
                //default local
                fs = FileSystem.getLocal(new Configuration());
            } else {
                fs = FileSystem.get(tmpPathURI, new Configuration());
            }
        } catch (IOException e) {
            LOGGER.error("Unable to access filesystem for path " + tmpPath + ". Aborting reporting", e);
            return;
        }

        Runnable task = new Runnable() {
            public void run() {
                try {
                    String json = null;
                    try {
                        json = jsonWriter.writeValueAsString(metricRegistry);
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Unable to convert json to string ", e);
                        return;
                    }

                    fs.delete(tmpPath, true);

                    try (BufferedWriter bw = new BufferedWriter(
                            new OutputStreamWriter(fs.create(tmpPath, true), StandardCharsets.UTF_8))) {
                        bw.write(json);
                        fs.setPermission(tmpPath, FsPermission.createImmutable((short) 0644));
                    } catch (IOException e) {
                        LOGGER.error("Unable to write to temp file " + tmpPath, e);
                        return;
                    }

                    try {
                        fs.rename(tmpPath, path);
                        fs.setPermission(path, FsPermission.createImmutable((short) 0644));
                    } catch (IOException e) {
                        LOGGER.error("Unable to rename temp file " + tmpPath + " to " + pathString, e);
                        return;
                    }
                } catch (Throwable t) {
                    // catch all errors (throwable and execptions to prevent subsequent tasks from being suppressed)
                    LOGGER.error("Error executing scheduled task ", t);
                }
            }
        };

        executorService.scheduleWithFixedDelay(task, 0, frequency, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
