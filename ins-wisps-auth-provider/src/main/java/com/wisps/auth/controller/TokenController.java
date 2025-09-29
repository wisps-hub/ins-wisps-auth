package com.wisps.auth.controller;

import com.wisps.cache.client.ICache;
import com.wisps.resp.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("token")
public class TokenController {

    @Autowired
    private ICache redisClient;

    @GetMapping("/parse")
    public Result<String> parse(@NotBlank String scene, @NotBlank String key) {
        //todo wisps
        return Result.success("");
    }

    @GetMapping("/get")
    public Result<String> get(@NotBlank String token) {
        //todo wisps
        return Result.success("");
    }
}