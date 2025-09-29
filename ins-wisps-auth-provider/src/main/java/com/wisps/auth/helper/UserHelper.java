package com.wisps.auth.helper;

import com.wisps.user.api.req.RegisterReq;
import com.wisps.user.api.req.UserQueryReq;
import com.wisps.user.api.resp.UserDto;
import com.wisps.user.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserHelper {

    @DubboReference(version = "1.0.0")
    private UserService userService;

    public UserDto register(RegisterReq registerReq){
        return userService.register(registerReq);
    }

    public UserDto queryOne(UserQueryReq queryReq){
        return userService.queryOne(queryReq);
    }
}