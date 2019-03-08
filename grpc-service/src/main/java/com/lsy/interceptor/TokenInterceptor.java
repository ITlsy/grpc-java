package com.lsy.interceptor;

import com.lsy.util.JwtUtil;
import io.grpc.*;
import io.grpc.internal.NoopServerCall;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

/**
 * Created by lsy on 2019/3/7.
 */
@Component
public class TokenInterceptor implements ServerInterceptor {


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String token=metadata.get(Metadata.Key.of("token",Metadata.ASCII_STRING_MARSHALLER));

        if(StringUtil.isNullOrEmpty(token)){
            System.err.println("未收到客户端token，关闭此连接");
            serverCall.close(Status.DATA_LOSS,metadata);
        }
        if (!JwtUtil.verify(token)) {
            serverCall.close(Status.PERMISSION_DENIED, metadata);
            return new NoopServerCall.NoopServerCallListener<ReqT>();
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
