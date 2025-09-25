package com.wisps.auth.gw;

import cn.dev33.satoken.stp.StpUtil;
import com.wisps.auth.exception.AuthErrorCode;
import com.wisps.auth.exception.AuthException;
import com.wisps.web.utils.TokenUtil;
import com.wisps.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

import static com.wisps.cache.consts.WispsCacheConst.SEPARATOR;
import static com.wisps.web.utils.TokenUtil.TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("token")
public class GwTokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/get")
    public Result<String> get(@NotBlank String scene, @NotBlank String key) {
        if (StpUtil.isLogin()) {
            String userId = (String) StpUtil.getLoginId();
            //token:buy:29:10085
            String tokenKey = TOKEN_PREFIX + scene + SEPARATOR + userId + SEPARATOR + key;
            String tokenValue = TokenUtil.getTokenValueByKey(tokenKey);
            //key：token:buy:29:10085
            //value：YZdkYfQ8fy7biSTsS5oZrbsB8eN7dHPgtCV0dw/36AHSfDQzWOj+ULNEcMluHvep/txjP+BqVRH3JlprS8tWrQ==
            stringRedisTemplate.opsForValue().set(tokenKey, tokenValue, 30, TimeUnit.MINUTES);
            return Result.success(tokenValue);
        }
        throw new AuthException(AuthErrorCode.USER_NOT_LOGIN);
    }
}
