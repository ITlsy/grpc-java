package com.lsy.controller;

import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lsy on 2019/3/8.
 */
@RestController
public class UserController {

    @Autowired
    private  UserServiceGrpc.UserServiceBlockingStub  userService;

    @Autowired
    private LoginServiceGrpc.LoginServiceBlockingStub loginService;
}
