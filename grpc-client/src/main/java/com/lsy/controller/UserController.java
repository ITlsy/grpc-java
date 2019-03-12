package com.lsy.controller;

import com.google.common.collect.Maps;
import com.lsy.grpc.api.UserServiceGrpc;
import com.lsy.grpc.api.UserServiceProto;
import com.lsy.grpc.api.login.LoginServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by lsy on 2019/3/8.
 */
@RestController
public class UserController {

    @Autowired
    private static UserServiceGrpc.UserServiceBlockingStub  userService;

    @Autowired
    private LoginServiceGrpc.LoginServiceBlockingStub loginService;


    @RequestMapping("/addUser")
    public static Map<String,Object> addUser(){
        UserServiceProto.User user=UserServiceProto.User.newBuilder()
                .setName("grpc").setPassword("123456").setAge(10).build();
        UserServiceProto.ResponseModel responseModel=userService.addUser(user);
        Map<String, Object> res = Maps.newHashMap();
        res.put("code", responseModel.getCode());
        res.put("msg", responseModel.getMsg());
        return res;
    }
}
