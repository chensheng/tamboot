package com.tamboot.webapp.security;

public enum SecurityRedisNamespace {
    TOKEN("token", "user login token"),
    CONFIG("config", "security meta information");

    private String code;

    private String msg;

    SecurityRedisNamespace(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
