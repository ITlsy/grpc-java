package com.lsy;

import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;

/**
 * Created by lsy on 2019/3/5.
 */
public class UserServer {
    private static final int DEFAULT_PROT=50051;

    private int port;

    private Server server;
    public UserServer(int port) {
        this(port,ServerBuilder.forPort(port));
    }

    public UserServer(int port, ServerBuilder<?> serverBuilder){

        this.port = port;
        server = serverBuilder.addService(ServerInterceptors.intercept(UserServiceGrpc.bindService(new UserServiceImpl())))
                .build();
        server = serverBuilder.addService(ServerInterceptors.intercept(LoginServiceGrpc.bindService(new LoginServiceImpl())))
                .build();
    }

    private void start() throws IOException {
        server.start();
        System.out.println("Server has started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                UserServer.this.stop();

            }
        });
    }

    private void stop() {
        if(server!=null){
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException{
        if(server!=null){
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        UserServer server=new UserServer(DEFAULT_PROT);
        server.start();
        server.blockUntilShutdown();
    }

}
