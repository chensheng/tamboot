package com.tamboot.common.utils;

public class RedisKeyFactory {
    private static final String KEY_SEPARATOR = ":";

    public static String create(String namespace, String key) {
        return namespace + KEY_SEPARATOR + key;
    }
}
