package com.tamboot.webapp.core;

public enum ResponseType {
    SUCCESS("0", "success"),
    FAIL("1", "fail"),
    EXCEPTION("9999", "exception"),
    NO_AUTHENTICATION("1001", "no authentication"),
    ACCESS_DENIED("1002", "access denied");

    private String code;

    private String msg;

    ResponseType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
