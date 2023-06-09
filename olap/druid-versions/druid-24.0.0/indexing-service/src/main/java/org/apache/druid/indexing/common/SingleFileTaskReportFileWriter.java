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

package org.apache.druid.indexing.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.druid.java.util.common.FileUtils;
import org.apache.druid.java.util.common.jackson.JacksonUtils;
import org.apache.druid.java.util.common.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class SingleFileTaskReportFileWriter implements TaskReportFileWriter
{
  private static final Logger log = new Logger(SingleFileTaskReportFileWriter.class);

  private final File reportsFile;
  private ObjectMapper objectMapper;

  public SingleFileTaskReportFileWriter(File reportsFile)
  {
    this.reportsFile = reportsFile;
  }

  @Override
  public void write(String taskId, Map<String, TaskReport> reports)
  {
    try {
      final File reportsFileParent = reportsFile.getParentFile();
      if (reportsFileParent != null) {
        FileUtils.mkdirp(reportsFileParent);
      }

      try (final FileOutputStream outputStream = new FileOutputStream(reportsFile)) {
        writeReportToStream(objectMapper, outputStream, reports);
      }
    }
    catch (Exception e) {
      log.error(e, "Encountered exception in write().");
    }
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper)
  {
    this.objectMapper = objectMapper;
  }

  public static void writeReportToStream(
      final ObjectMapper objectMapper,
      final OutputStream outputStream,
      final Map<String, TaskReport> reports
  ) throws Exception
  {
    final SerializerProvider serializers = objectMapper.getSerializerProviderInstance();

    try (final JsonGenerator jg = objectMapper.getFactory().createGenerator(outputStream)) {
      jg.writeStartObject();

      for (final Map.Entry<String, TaskReport> entry : reports.entrySet()) {
        jg.writeFieldName(entry.getKey());
        JacksonUtils.writeObjectUsingSerializerProvider(jg, serializers, entry.getValue());
      }

      jg.writeEndObject();
    }
  }
}
