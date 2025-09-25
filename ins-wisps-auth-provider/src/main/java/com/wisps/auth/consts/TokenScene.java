package com.wisps.auth.consts;

import java.util.Arrays;

/**
 * token获取的场景枚举
 */
public enum TokenScene {
    /**
     * 下单-藏品
     */
    BUY_COLLECTION("token:buy:clc"),

    /**
     * 下单-盲盒
     */
    BUY_BLIND_BOX("token:buy:blb");

    /**
     * 场景的值
     */
    private String scene;

    TokenScene(String scene) {
        this.scene = scene;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public static TokenScene getByScene(String scene) {
        return Arrays.stream(TokenScene.values())
                .filter(tokenScene -> tokenScene.getScene().equals(scene))
                .findFirst().orElse(null);
    }
}
