package com.wisps.auth.vo;

import cn.dev33.satoken.stp.StpUtil;
import com.wisps.user.api.resp.data.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标识，如用户ID
     */
    private String userId;
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private Long tokenExpiration;


    public LoginVO(UserInfo userInfo) {
        this.userId = userInfo.getUserId().toString();
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }
}
