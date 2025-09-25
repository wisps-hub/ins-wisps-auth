package com.wisps.auth.consts;

import static com.wisps.web.utils.TokenUtil.TOKEN_PREFIX;

public class Consts {

    private static final String CACHE_KEY_FORMAT = "%s:%s:%s";
    public static final Long EXP_30 = 1000 * 60 * 30L;

    public static String tokenKey(String scene, String uid, String key){
        //token:scene:uid:key
        return TOKEN_PREFIX + String.format(CACHE_KEY_FORMAT, scene, uid, key);
    }

}