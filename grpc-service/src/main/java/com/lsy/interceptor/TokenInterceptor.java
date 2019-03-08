package com.lsy.interceptor;

import com.lsy.util.JwtUtil;
import io.grpc.*;
import io.grpc.internal.NoopServerCall;
import org.springframework.stereotype.Component;

/**
 * Created by lsy on 2019/3/7.
 */
@Component
public class TokenInterceptor implements ServerInterceptor {

    public static final String TOKEN_ENDPOINT="com.lsy.grpc.api.auth.LoginService/Verify";
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String fullMethodName=serverCall.getMethodDescriptor().getFullMethodName();
        if (TOKEN_ENDPOINT.equals(fullMethodName)) {
            return serverCallHandler.startCall(serverCall, metadata);
        }

        String token=metadata.get(Metadata.Key.of("token",Metadata.ASCII_STRING_MARSHALLER));
        if (!JwtUtil.verify(token)) {
            serverCall.close(Status.PERMISSION_DENIED, metadata);
            return new NoopServerCall.NoopServerCallListener<ReqT>();
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
