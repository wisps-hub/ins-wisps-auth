package com.wisps.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.wisps.auth.exception.AuthException;
import com.wisps.auth.param.LoginParam;
import com.wisps.auth.param.RegisterParam;
import com.wisps.auth.vo.LoginVO;
import com.wisps.base.validator.IsMobile;
import com.wisps.notice.api.resp.NoticeResp;
import com.wisps.notice.api.service.NoticeService;
import com.wisps.user.api.req.UserQueryReq;
import com.wisps.user.api.req.UserRegisterReq;
import com.wisps.user.api.resp.UserOpResp;
import com.wisps.user.api.resp.UserQueryResp;
import com.wisps.user.api.resp.data.UserInfo;
import com.wisps.web.vo.Result;
import com.wisps.chain.api.service.ChainService;
import com.wisps.user.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import static com.wisps.auth.exception.AuthErrorCode.VERIFICATION_CODE_WRONG;
import static com.wisps.notice.api.consts.NoticeConst.CAPTCHA_KEY_PREFIX;

/**
 * 认证相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference(version = "1.0.0")
    private UserService userService;

    @DubboReference(version = "1.0.0")
    private NoticeService noticeService;

    @DubboReference(version = "1.0.0")
    private ChainService chainService;

    private static final String ROOT_CAPTCHA = "8888";

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    @GetMapping("/sendCaptcha")
    public Result<Boolean> sendCaptcha(@IsMobile String telephone) {
        NoticeResp noticeResp = noticeService.generateAndSendSmsCaptcha(telephone);
        return Result.success(noticeResp.getSuccess());
    }

    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {

        //验证码校验
        String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
            throw new AuthException(VERIFICATION_CODE_WRONG);
        }

        //注册
        UserRegisterReq userRegisterReq = new UserRegisterReq();
        userRegisterReq.setTelephone(registerParam.getTelephone());
        userRegisterReq.setInviteCode(registerParam.getInviteCode());

        UserOpResp registerResult = userService.register(userRegisterReq);
        if(registerResult.getSuccess()){
            return Result.success(true);
        }
        return Result.error(registerResult.getRespCode(), registerResult.getRespMsg());
    }

    /**
     * 登录方法
     *
     * @param loginParam 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {
        //fixme 为了方便，暂时直接跳过
        if (!ROOT_CAPTCHA.equals(loginParam.getCaptcha())) {
            //验证码校验
            String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + loginParam.getTelephone());
            if (!StringUtils.equalsIgnoreCase(cachedCode, loginParam.getCaptcha())) {
                throw new AuthException(VERIFICATION_CODE_WRONG);
            }
        }

        //判断是注册还是登陆
        //查询用户信息
        UserQueryReq userQueryReq = new UserQueryReq(loginParam.getTelephone());
        UserQueryResp<UserInfo> infoUserQueryResp = userService.query(userQueryReq);
        UserInfo userInfo = infoUserQueryResp.getData();
        if (userInfo == null) {
            //需要注册
            UserRegisterReq userRegisterReq = new UserRegisterReq();
            userRegisterReq.setTelephone(loginParam.getTelephone());
            userRegisterReq.setInviteCode(loginParam.getInviteCode());

            UserOpResp response = userService.register(userRegisterReq);
            if (response.getSuccess()) {
                infoUserQueryResp = userService.query(userQueryReq);
                userInfo = infoUserQueryResp.getData();
                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                LoginVO loginVO = new LoginVO(userInfo);
                return Result.success(loginVO);
            }

            return Result.error(response.getRespCode(), response.getRespMsg());
        } else {
            //登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            LoginVO loginVO = new LoginVO(userInfo);
            return Result.success(loginVO);
        }
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
