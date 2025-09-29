package com.wisps.auth.vo.req;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginReqVo extends RegisterReqVo {

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
