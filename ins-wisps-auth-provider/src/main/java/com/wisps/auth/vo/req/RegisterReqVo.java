package com.wisps.auth.vo.req;

import com.wisps.req.BaseReq;
import com.wisps.validator.IsMobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterReqVo extends BaseReq {
    /**
     * 手机号
     */
    @IsMobile
    private String mobile;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String captcha;
    /**
     * 邀请码
     */
    private String inviteCode;
}