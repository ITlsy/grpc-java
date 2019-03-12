package com.lsy;

import com.lsy.grpc.api.HelloServiceGrpc;
import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import com.lsy.util.TraceUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.opentracing.contrib.grpc.ServerTracingInterceptor;
import io.opentracing.util.GlobalTracer;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by lsy on 2019/3/5.
 */
public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());

    private static final int DEFAULT_PROT=50051;

    private int port;

    private Server server;

    public UserServer(int port) {
        this(port,ServerBuilder.forPort(port));
    }

    public UserServer(int port, ServerBuilder<?> serverBuilder){
        ServerTracingInterceptor tracingInterceptor=new ServerTracingInterceptor(GlobalTracer.get());

        this.port = port;
        server = serverBuilder.addService(ServerInterceptors.intercept(tracingInterceptor.intercept(LoginServiceGrpc.bindService(new LoginServiceImpl()))))
                .build();

        server = serverBuilder.addService(ServerInterceptors.intercept(tracingInterceptor.intercept(UserServiceGrpc.bindService(new UserServiceImpl()))))
                .build();

        server = serverBuilder.addService(ServerInterceptors.intercept(HelloServiceGrpc.bindService(new GwHelloServiceImpl())))
                .build();

        //server =ServerBuilder.forPort(port).addService(tracingInterceptor.intercept(UserServiceGrpc.bindService(new UserServiceImpl()))).build();
        //server =ServerBuilder.forPort(port).addService(tracingInterceptor.intercept(LoginServiceGrpc.bindService(new LoginServiceImpl()))).build();


    }

    private void start() throws IOException {
        server.start();
        logger.info("Server started, listening on " + port);
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
        TraceUtils.registerTracer("grpc-server");
        UserServer server=new UserServer(DEFAULT_PROT);
        server.start();
        server.blockUntilShutdown();
    }

}
