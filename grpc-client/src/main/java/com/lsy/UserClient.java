package com.lsy;

import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.UserServiceProto;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceProto;
import com.sun.xml.internal.ws.util.MetadataUtil;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsy on 2019/3/5.
 */
public class UserClient {

    private static final String DEFAULT_HOST = "127.0.0.1";

    private static final int DEFAULT_PORT = 50051;

    private String token;

    private final ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub userService;
    private LoginServiceGrpc.LoginServiceBlockingStub loginService;


    public UserClient(String host,int port){
        channel= ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(new ClientInterceptor() {
                    @Override
                    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
                        Metadata metadata=new Metadata();
                        if(token!=null){
                            metadata.put(Metadata.Key.of("token",Metadata.ASCII_STRING_MARSHALLER),token);
                        }
                        return MetadataUtils.newAttachHeadersInterceptor(metadata).interceptCall(methodDescriptor, callOptions, channel);
                    }
                })
                .build();

        loginService=LoginServiceGrpc.newBlockingStub(channel);
        LoginServiceProto.ResponseModel responseModel=loginService.checkLogin(
                LoginServiceProto.Verify.newBuilder()
                .setUsername("lsy")
                .setPassword("666666")
                .build()
        );
        if (responseModel.getCode()) {
            this.token = responseModel.getToken();
            System.out.println("token "+token);
        }else{
            System.out.println("token获取失败");
        }
        userService=UserServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Map<String,UserServiceProto.User> getWay(int userId,String userName,String password,int age){
        LoginServiceProto.Verify verify=LoginServiceProto.Verify.newBuilder()
                .setUsername(userName)
                .setPassword(password)
                .build();
        loginService.checkLogin(verify);
        UserServiceProto.User user=UserServiceProto.User.newBuilder()
                .setName(userName)
                .setPassword(password)
                .setAge(age)
                .build();
         UserServiceProto.ResponseModel responseModel=userService.addUser(user);
         UserServiceProto.Integer id=UserServiceProto.Integer.newBuilder().setUserId(userId).build();
         String  name=userService.getUserById(id).getUserDataName();

        Map<String,UserServiceProto.User> data=new HashMap<String, UserServiceProto.User>();
        data.put(name,user);
        return data;

    }

    public static void main(String[] args) throws InterruptedException {
        UserClient client=new UserClient(DEFAULT_HOST,DEFAULT_PORT);
        Map<String,UserServiceProto.User> data=new HashMap<String, UserServiceProto.User>();
        data=client.getWay(1,"lsy","666666",18);
        System.out.println(data);
        client.shutdown();

    }
}
