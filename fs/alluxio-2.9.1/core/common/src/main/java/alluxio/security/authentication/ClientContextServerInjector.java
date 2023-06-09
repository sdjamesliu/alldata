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

package alluxio.security.authentication;

import alluxio.grpc.ClientVersionClientInjector;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import javax.annotation.Nullable;

/**
 * Server side interceptor that is used to put remote client's IP Address to thread local storage.
 */
public class ClientContextServerInjector implements ServerInterceptor {

  /**
   * A {@link ThreadLocal} variable to maintain the client's IP address along with a specific
   * thread.
   */
  private static final ThreadLocal<String> IP_ADDRESS_THREAD_LOCAL = new ThreadLocal<>();
  /**
   * A {@link ThreadLocal} variable to maintain the client's version along with a specific
   * thread.
   */
  private static final ThreadLocal<String> CLIENT_VERSION_THREAD_LOCAL =
      new ThreadLocal<>();

  /**
   * @return IP address of the gRPC client that is making the call
   */
  @Nullable
  public static String getIpAddress() {
    return IP_ADDRESS_THREAD_LOCAL.get();
  }

  /**
   * @return the client version
   */
  @Nullable
  public static String getClientVersion() {
    return CLIENT_VERSION_THREAD_LOCAL.get();
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
      Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    /**
     * For streaming calls, below will make sure remote IP address and client version are
     * injected prior to creating the stream.
     */
    setRemoteIpAddress(call);
    setClientVersion(headers);

    /**
     * For non-streaming calls to server, below listener will be invoked in the same thread that is
     * serving the call.
     */
    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
        next.startCall(call, headers)) {
      @Override
      public void onHalfClose() {
        setRemoteIpAddress(call);
        setClientVersion(headers);
        super.onHalfClose();
      }
    };
  }

  private <ReqT, RespT> void setRemoteIpAddress(ServerCall<ReqT, RespT> call) {
    String remoteIpAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString();
    IP_ADDRESS_THREAD_LOCAL.set(remoteIpAddress);
  }

  private void setClientVersion(Metadata headers) {
    String version = headers.get(ClientVersionClientInjector.S_CLIENT_VERSION_KEY);
    CLIENT_VERSION_THREAD_LOCAL.set(version);
  }
}
