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

package org.apache.druid.storage.s3;

import com.amazonaws.auth.AWSSessionCredentials;
import com.google.common.io.Files;
import org.apache.druid.common.aws.FileSessionCredentialsProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestFileSessionCredentialsProvider
{
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void test() throws IOException
  {
    File file = folder.newFile();
    try (BufferedWriter out = Files.newWriter(file, StandardCharsets.UTF_8)) {
      out.write("sessionToken=sessionTokenSample\nsecretKey=secretKeySample\naccessKey=accessKeySample\n");
    }

    FileSessionCredentialsProvider provider = new FileSessionCredentialsProvider(file.getAbsolutePath());
    AWSSessionCredentials sessionCredentials = (AWSSessionCredentials) provider.getCredentials();
    Assert.assertEquals("sessionTokenSample", sessionCredentials.getSessionToken());
    Assert.assertEquals("accessKeySample", sessionCredentials.getAWSAccessKeyId());
    Assert.assertEquals("secretKeySample", sessionCredentials.getAWSSecretKey());
  }
}
