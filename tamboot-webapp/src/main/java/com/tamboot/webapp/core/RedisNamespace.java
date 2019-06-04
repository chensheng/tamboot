package com.tamboot.webapp.core;

public enum RedisNamespace {
    TOKEN("token"),
    CONFIG("config");

    private String value;

    RedisNamespace(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
