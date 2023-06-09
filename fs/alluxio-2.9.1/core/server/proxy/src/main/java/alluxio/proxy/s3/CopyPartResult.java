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

package alluxio.proxy.s3;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Result of an S3 UploadPartCopy.
 */
@JacksonXmlRootElement(localName = "CopyPartResult")
public class CopyPartResult {

  private final String mETag;

  /**
   * Create a new {@link CopyPartResult}.
   *
   * @param etag etag included in the result
   */
  public CopyPartResult(String etag) {
    mETag = etag;
  }

  /**
   * @return the ETag
   */
  @JacksonXmlProperty(localName = "ETag")
  public String getEtag() {
    return mETag;
  }
}
