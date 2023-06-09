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

package org.apache.druid.indexing.overlord.sampler;

import com.google.common.collect.ImmutableMap;
import org.apache.druid.java.util.common.logger.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SamplerExceptionMapper implements ExceptionMapper<SamplerException>
{
  private static final Logger LOG = new Logger(SamplerExceptionMapper.class);

  @Override
  public Response toResponse(SamplerException exception)
  {
    String message = exception.getMessage() == null ? "The sampler encountered an issue" : exception.getMessage();

    // Logging the stack trace and returning the exception message in the response
    LOG.error(exception, message);

    return Response.status(Response.Status.BAD_REQUEST)
                   .entity(ImmutableMap.of(
                       "error",
                       message
                   ))
                   .build();
  }
}
