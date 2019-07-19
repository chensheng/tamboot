package com.tamboot.redis.core;

import com.tamboot.common.tools.text.TextUtil;

public class RedisKeyGenerator {
    private static final String SEPARATOR = ":";

    public static String generate(String... keyUnits) {
        StringBuilder key = new StringBuilder();
        if (keyUnits == null || keyUnits.length == 0) {
            return key.toString();
        }

        boolean isFirstKeyUnit = true;
        for (String keyUnit : keyUnits) {
            if (TextUtil.isEmpty(keyUnit)) {
                continue;
            }

            if (isFirstKeyUnit) {
                isFirstKeyUnit = false;
            } else {
                key.append(SEPARATOR);
            }
            key.append(keyUnit);
        }
        return key.toString();
    }
}
