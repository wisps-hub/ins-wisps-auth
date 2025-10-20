package com.wisps.auth.vo.resp;

import com.wisps.req.BaseReq;
import com.wisps.user.api.resp.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginVo extends BaseReq {
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private Long tokenExpire;


    public LoginVo(UserDto userDto) {
        this.uid = userDto.getId().toString();
        this.token = "";
        this.tokenExpire = -1L;
    }
}
