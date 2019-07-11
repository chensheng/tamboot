package com.tamboot.rocketmq.client.core;

public enum MessageDelayLevel {
    DELAY_1S(1, "1s"),
    DELAY_5S(2, "5s"),
    DELAY_10S(3, "10s"),
    DELAY_30S(4, "30s"),
    DELAY_1M(5, "1m"),
    DELAY_2M(6, "2m"),
    DELAY_3M(7, "3m"),
    DELAY_4M(8, "4m"),
    DELAY_5M(9, "5m"),
    DELAY_6M(10, "6m"),
    DELAY_7M(11, "7m"),
    DELAY_8M(12, "8m"),
    DELAY_9M(13, "9m"),
    DELAY_10M(14, "10m"),
    DELAY_20M(15, "20m"),
    DELAY_30M(16, "30m"),
    DELAY_1H(17, "1h"),
    DELAY_2H(18, "2h");

    private int code;

    private String msg;

    MessageDelayLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
