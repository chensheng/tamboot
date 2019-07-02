package com.tamboot.redis.core;

public class RedisKeyGenerator {
    private static final String SEPARATOR = ":";

    public static String generate(String... keyUnits) {
        StringBuilder key = new StringBuilder();
        if (keyUnits == null || keyUnits.length == 0) {
            return key.toString();
        }

        for (int i = 0; i<keyUnits.length; i++) {
            if (i > 0) {
                key.append(SEPARATOR);
            }
            key.append(keyUnits[i]);
        }
        return key.toString();
    }
}
