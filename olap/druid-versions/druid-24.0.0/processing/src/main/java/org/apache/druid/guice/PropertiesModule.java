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

package org.apache.druid.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.apache.druid.java.util.common.logger.Logger;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class PropertiesModule implements Module
{
  private static final Logger log = new Logger(PropertiesModule.class);

  private final List<String> propertiesFiles;

  public PropertiesModule(List<String> propertiesFiles)
  {
    this.propertiesFiles = propertiesFiles;
  }

  @Override
  public void configure(Binder binder)
  {
    final Properties fileProps = new Properties();
    Properties systemProps = System.getProperties();

    Properties props = new Properties(fileProps);
    props.putAll(systemProps);

    for (String propertiesFile : propertiesFiles) {
      try (InputStream stream = openPropertiesFile(propertiesFile, systemProps)) {
        if (stream != null) {
          log.debug("Loading properties from %s", propertiesFile);
          try (final InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            fileProps.load(in);
          }
          catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    binder.bind(Properties.class).toInstance(props);
  }

  @Nullable
  private static InputStream openPropertiesFile(final String propertiesFile, final Properties systemProps)
      throws IOException
  {
    final InputStream stream = ClassLoader.getSystemResourceAsStream(propertiesFile);

    if (stream != null) {
      return stream;
    } else {
      File workingDirectoryFile = new File(systemProps.getProperty("druid.properties.file", propertiesFile));
      if (workingDirectoryFile.exists()) {
        return new BufferedInputStream(new FileInputStream(workingDirectoryFile));
      } else {
        return null;
      }
    }
  }
}
