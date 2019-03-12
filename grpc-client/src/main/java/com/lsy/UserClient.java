package com.lsy;

import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.UserServiceProto;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceProto;
import com.lsy.util.TraceUtils;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import io.opentracing.contrib.grpc.ClientTracingInterceptor;
import io.opentracing.util.GlobalTracer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by lsy on 2019/3/5.
 */
public class UserClient {
    private static final Logger logger = Logger.getLogger(UserClient.class.getName());


    private static final String DEFAULT_HOST = "127.0.0.1";

    private static final int DEFAULT_PORT = 50051;

    private String token;

    private final ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub userService;
    private LoginServiceGrpc.LoginServiceBlockingStub loginService;


    public UserClient(String host,int port){
        ClientTracingInterceptor tracingInterceptor = new ClientTracingInterceptor(GlobalTracer.get());

        //ClientTracingInterceptor tracingInterceptor=new ClientTracingInterceptor(this.tracer);

        channel= ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
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

        loginService=LoginServiceGrpc.newBlockingStub(tracingInterceptor.intercept(channel));
        userService=UserServiceGrpc.newBlockingStub(tracingInterceptor.intercept(channel));

    }

    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Map<String,UserServiceProto.ResponseModel> getWay(int userId,String userName,String password,int age){
        logger.info("Will try to getWay ...");

        LoginServiceProto.ResponseModel isAuthenticated=loginService.checkLogin(
                LoginServiceProto.Verify.newBuilder()
                        .setUsername(userName)
                        .setPassword(password)
                        .build()
        );
        if (isAuthenticated.getCode()) {
            this.token = isAuthenticated.getToken();
            System.out.println("token认证成功");
        }else{
            System.out.println("token获取失败");
        }
        LoginServiceProto.Verify verify=LoginServiceProto.Verify.newBuilder()
                .setUsername(userName)
                .setPassword(password)
                .build();
        LoginServiceProto.ResponseModel result=loginService.checkLogin(verify);
        System.out.println(result);
        UserServiceProto.User user=UserServiceProto.User.newBuilder()
                .setName(userName)
                .setPassword(password)
                .setAge(age)
                .build();
         UserServiceProto.ResponseModel responseModel=userService.addUser(user);
         UserServiceProto.Integer id=UserServiceProto.Integer.newBuilder().setUserId(userId).build();
         String  name=userService.getUserById(id).getUserDataName();

        Map<String,UserServiceProto.ResponseModel> data=new HashMap<String, UserServiceProto.ResponseModel>();
        /*Map<String, Object> map = Maps.newHashMap();
        map=UserController.addUser();*/
        data.put(name,responseModel);
        return data;


    }

    public static void main(String[] args) throws InterruptedException {
        TraceUtils.registerTracer("grpc-client");
        UserClient client=new UserClient(DEFAULT_HOST,DEFAULT_PORT);
        Map<String,UserServiceProto.ResponseModel> data=new HashMap<String, UserServiceProto.ResponseModel>();
        data=client.getWay(1,"lsy","666666",18);
        System.out.println(data);
        client.shutdown();

    }
}
