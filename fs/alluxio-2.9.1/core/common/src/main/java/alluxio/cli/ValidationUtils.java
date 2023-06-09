/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.cli;

import alluxio.AlluxioURI;

/**
 * Utilities to run the validation tests.
 */
public final class ValidationUtils {

  private ValidationUtils() {} // prevent instantiation

  /**
   * Task State.
   */
  public enum State {
    OK,
    WARNING,
    FAILED,
    SKIPPED
  }

  /**
   * Checks if a path is HDFS.
   *
   * @param path the UFS path
   * @return true if the path is HDFS
   * */
  public static boolean isHdfsScheme(String path) {
    String scheme = new AlluxioURI(path).getScheme();
    return scheme != null && scheme.startsWith("hdfs");
  }
}
