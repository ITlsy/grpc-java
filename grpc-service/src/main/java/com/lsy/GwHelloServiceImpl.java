package com.lsy;

import com.lsy.grpc.api.HelloServiceGrpc;
import com.lsy.grpc.api.HelloServiceProto;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * Created by lsy on 2019/3/11.
 */
@Service
public class GwHelloServiceImpl implements HelloServiceGrpc.HelloService {
    @Override
    public void sayHello(HelloServiceProto.GwRequest request, StreamObserver<HelloServiceProto.Result> responseObserver) {
        HelloServiceProto.Result result= HelloServiceProto.Result.newBuilder().setCode(0).build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
