package com.lsy;


import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.UserServiceProto;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * Created by lsy on 2019/3/5.
 */
@Service
public class UserServiceImpl implements UserServiceGrpc.UserService{

    @Override
    public void addUser(UserServiceProto.User user, StreamObserver<UserServiceProto.ResponseModel> responseObserver) {
        String name=user.getName();
        String pwd=user.getPassword();
        int age=user.getAge();
        System.out.println(name);
        System.out.println(pwd);
        System.out.println(age);
        UserServiceProto.ResponseModel responseModel=UserServiceProto.ResponseModel.newBuilder().setCode(true).setMsg("success").build();
        responseObserver.onNext(responseModel);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(UserServiceProto.Integer Integer, StreamObserver<UserServiceProto.UserData> responseObserver) {
       // System.out.println(Integer);
        UserServiceProto.Integer userId =UserServiceProto.Integer.newBuilder()
                .setUserId(Integer.getUserId())
                .build();

        UserServiceProto.UserData userData=UserServiceProto.UserData.newBuilder()
                .setCode(true)
                .setMsg("success")
                .setUserDataName("lsy").build();
        responseObserver.onNext(userData);
        responseObserver.onCompleted();
    }

}
