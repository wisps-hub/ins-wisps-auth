package com.wisps.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.wisps.auth.consts.Consts;
import com.wisps.auth.exception.AuthErrorCode;
import com.wisps.auth.exception.AuthException;
import com.wisps.cache.client.ICache;
import com.wisps.web.utils.TokenUtil;
import com.wisps.resp.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("token")
public class TokenController {

    @Autowired
    private ICache redisClient;

    @GetMapping("/get")
    public Result<String> get(@NotBlank String scene, @NotBlank String key) {
        if (!StpUtil.isLogin()) {
            throw new AuthException(AuthErrorCode.USER_NOT_LOGIN);
        }
        //key：token:buy:29:10085
        String tokenKey = Consts.tokenKey(scene, (String) StpUtil.getLoginId(), key);
        //value：YZdkYfQ8fy7biSTsS5oZrbsB8eN7dHPgtCV0dw/36AHSfDQzWOj+ULNEcMluHvep/txjP+BqVRH3JlprS8tWrQ==
        String tokenValue = TokenUtil.getTokenValueByKey(tokenKey);
        redisClient.set(tokenKey, Consts.EXP_30, tokenValue);
        return Result.success(tokenValue);
    }
}