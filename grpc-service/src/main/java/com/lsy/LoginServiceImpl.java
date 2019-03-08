package com.lsy;

import com.lsy.grpc.api.login.LoginServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceProto;
import com.lsy.util.JwtUtil;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * Created by lsy on 2019/3/8.
 */
@Service
public class LoginServiceImpl implements LoginServiceGrpc.LoginService {

    @Override
    public void checkLogin(LoginServiceProto.Verify verify, StreamObserver<LoginServiceProto.ResponseModel> responseObserver) {
        String userName=verify.getUsername();
        String password=verify.getPassword();

        if("lsy".equalsIgnoreCase(userName) && "666666".equalsIgnoreCase(password)){
            LoginServiceProto.ResponseModel responseModel=LoginServiceProto.ResponseModel.newBuilder().setCode(true)
                    .setMsg("login success")
                    .setToken(JwtUtil.createJwt(userName,password))
                    .build();
            responseObserver.onNext(responseModel);
        }else {
            LoginServiceProto.ResponseModel responseModel=LoginServiceProto.ResponseModel.newBuilder().setCode(false)
                    .setMsg("not authenticated")
                    .build();
            responseObserver.onNext(responseModel);
        }
        responseObserver.onCompleted();
    }
}
