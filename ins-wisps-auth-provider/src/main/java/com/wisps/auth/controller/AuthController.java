package com.wisps.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.wisps.auth.exception.AuthException;
import com.wisps.auth.helper.NoticeHelper;
import com.wisps.auth.helper.UserHelper;
import com.wisps.auth.vo.req.LoginReqVo;
import com.wisps.auth.vo.req.RegisterReqVo;
import com.wisps.auth.vo.resp.LoginVo;
import com.wisps.cache.client.ICache;
import com.wisps.notice.api.consts.NoticeConst;
import com.wisps.resp.Result;
import com.wisps.user.api.req.RegisterReq;
import com.wisps.user.api.req.UserQueryReq;
import com.wisps.user.api.resp.UserDto;
import com.wisps.validator.IsMobile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.wisps.auth.exception.AuthErrorCode.USER_OPERATE_FAILED;
import static com.wisps.auth.exception.AuthErrorCode.VERIFICATION_CODE_WRONG;

/**
 * 认证相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;
    private static final String ROOT_CAPTCHA = "8888";

    @Autowired
    private ICache redisClient;
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private NoticeHelper noticeHelper;

    @GetMapping("/sendCaptcha/{mobile}")
    public Result<Boolean> sendCaptcha(@PathVariable("mobile") @IsMobile String mobile) {
        return Result.success(noticeHelper.genSendSmsCaptcha(mobile));
    }

    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterReqVo registerReqVo) {
        //验证码校验
        String cachedCaptcha = redisClient.getString(NoticeConst.captchaKey(registerReqVo.getMobile()));
        if (!StringUtils.equalsIgnoreCase(cachedCaptcha, registerReqVo.getCaptcha())) {
            throw new AuthException(VERIFICATION_CODE_WRONG);
        }
        //注册
        RegisterReq registerReq = new RegisterReq();
        registerReq.setMobile(registerReq.getMobile());
        registerReq.setInviteCode(registerReq.getInviteCode());
        UserDto userDto = userHelper.register(registerReq);
        return Result.success(userDto != null);
    }

    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginReqVo loginReq) {
        if (!ROOT_CAPTCHA.equals(loginReq.getCaptcha())) {
            // 验证码校验
            String cachedCaptcha = redisClient.getString(NoticeConst.captchaKey(loginReq.getMobile()));
            if (!StringUtils.equalsIgnoreCase(cachedCaptcha, loginReq.getCaptcha())) {
                throw new AuthException(VERIFICATION_CODE_WRONG);
            }
        }
        // 查询用户信息
        UserQueryReq userQueryReq = new UserQueryReq(null, loginReq.getMobile(), "");
        UserDto userDto = userHelper.queryOne(userQueryReq);
        if (userDto == null) {
            // 注册
            RegisterReq registerReq = new RegisterReq();
            registerReq.setMobile(loginReq.getMobile());
            registerReq.setInviteCode(loginReq.getInviteCode());
            userDto = userHelper.register(registerReq);
            if (userDto == null) {
                return Result.error(USER_OPERATE_FAILED.getCode(), USER_OPERATE_FAILED.getMsg());
            }
        }
        // 登录
        StpUtil.login(userDto.getId(), new SaLoginModel()
                .setIsLastingCookie(loginReq.getRememberMe())
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
        StpUtil.getSession().set(userDto.getId().toString(), userDto);
        return Result.success(new LoginVo(userDto));
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();
        return Result.success(true);
    }

    @RequestMapping("test")
    public String test() {
        return "test";
    }

}
